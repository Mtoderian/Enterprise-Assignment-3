package io;

import chatterboxserver.ServerModel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sharedobjects.ChatRoom;

/**
 * Processes the input of a single connection
 */
public class ConnectionProcessor implements Runnable {

    private final IOHandler ioh;
    private final ServerModel sm;
    private String user;
    private String chatRoom;

    /**
     * States the ConnectionProcessor can be in.
     */
    public static enum State {

        INIT, CHATROOMLIST, CHATROOM
    }
    private State state;

    /**
     * constructor for the ConnectionProcessor
     *
     * @param ioh IOHandler of the connection
     * @param sm the used ServerModel
     */
    public ConnectionProcessor(IOHandler ioh, ServerModel sm) {
        this.ioh = ioh;
        this.sm = sm;
        state = State.INIT;
    }

    /**
     * Keeps processing the input of the connection until the connection closes.
     */
    @Override
    public void run() {
        try {
            while (true) {
                Object o = ioh.receive();
                switch (state) {
                    case INIT:
                        if (o instanceof String) {
                            String username = (String) o;
                            Boolean added = sm.addUser(username, ioh);
                            synchronized (ioh) {
                                ioh.send(added);
                                if (added) {
                                    user = username;
                                    state = State.CHATROOMLIST;
                                    ioh.send(sm.getChatRooms().toArray(new ChatRoom[0]));
                                }
                            }
                        }
                        break;
                    case CHATROOMLIST:
                        if (o instanceof String) {
                            String[] cmd = ((String) o).split(" ", 2);
                            switch (cmd[0]) {
                                case "refreshChatRoomList":
                                    ioh.send(sm.getChatRooms().toArray(new ChatRoom[0]));
                                    break;
                                case "createChatRoom":
                                    Boolean added = sm.addChatRoom(cmd[1], user);
                                    synchronized (ioh) {
                                        if (added) {
                                            chatRoom = cmd[1];
                                            state = State.CHATROOM;
                                            ioh.send(sm.getChatRoom(chatRoom));
                                            ioh.send(sm.getChatRoomUsers(chatRoom).toArray(new String[0]));
                                        } else {
                                            ioh.send("error " + "chat room with this name already exists");
                                        }
                                    }
                                    break;
                                case "joinChatRoom":
                                    Boolean joined = sm.joinChatRoom(cmd[1], user);
                                    synchronized (ioh) {

                                        if (joined) {
                                            chatRoom = cmd[1];
                                            state = State.CHATROOM;
                                            ioh.send(sm.getChatRoom(chatRoom));
                                            String[] users = sm.getChatRoomUsers(chatRoom).toArray(new String[0]);
                                            for (String u : users) {
                                                sm.getUserIOHandler(u).send(users);
                                            }
                                        } else {
                                            ioh.send("error " + "could not join chat room");
                                            ioh.send(sm.getChatRooms().toArray(new ChatRoom[0]));
                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                    case CHATROOM:
                        if (o instanceof String) {
                            String[] cmd = ((String) o).split(" ", 2);
                            switch (cmd[0]) {
                                case "msg":
                                    for (String u : sm.getChatRoomUsers(chatRoom)) {
                                        sm.getUserIOHandler(u).send(cmd[0] + " " + user + " " + cmd[1]);
                                    }
                                    break;
                                case "leaveChatRoom":
                                    if (sm.leaveChatRoom(chatRoom, user)) {
                                        String[] users = sm.getChatRoomUsers(chatRoom).toArray(new String[0]);
                                        for (String u : users) {
                                            sm.getUserIOHandler(u).send(users);
                                        }
                                    }
                                    chatRoom = null;
                                    state = State.CHATROOMLIST;
                                    ioh.send(sm.getChatRooms().toArray(new ChatRoom[0]));
                                    break;
                            }
                        }
                        break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger("ChatterBoxServerLog").log(Level.INFO, "User disconnected: {0}", ex.toString());
            ioh.close();
        } finally {
            if (!state.equals(State.INIT)) {
                sm.removeUser(user);
                if (state.equals(State.CHATROOM)) {
                    sm.leaveChatRoom(chatRoom, user);
                }
            }
        }
    }
}
