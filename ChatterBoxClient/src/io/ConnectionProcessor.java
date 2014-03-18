package io;

import chatroom.ChatRoomFrame;
import chatroom.ChatRoomModel;
import chatroomlist.ChatRoomListFrame;
import chatroomlist.ChatRoomListModel;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JOptionPane;
import sharedobjects.ChatRoom;

/**
 * Processes the input of a single connection
 */
public class ConnectionProcessor implements Runnable {

    private final IOHandler ioh;
    private final ChatRoomListModel crlm;
    private ChatRoomModel crm;
    private final Object crmLock = new Object();
    private final ChatRoomListFrame crlf;
    private ChatRoomFrame crf;

    /**
     * Constructor for ConnectionProcessor
     *
     * @param ioh IOHandler of the connection
     * @param crlm the chat room list model
     * @param crlf the chat room list frame
     */
    public ConnectionProcessor(IOHandler ioh, ChatRoomListModel crlm, ChatRoomListFrame crlf) {
        this.ioh = ioh;
        this.crlm = crlm;
        this.crlf = crlf;
    }

    /**
     * Starts the ConnectionProcessor
     */
    public void start() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Sets the model of the joined chat room
     *
     * @param crm
     */
    public void setChatRoomModel(ChatRoomModel crm) {
        this.crm = crm;
        synchronized (crmLock) {
            crmLock.notifyAll();
        }
    }

    /**
     * Keeps processing the input of the connection until the connection closes.
     */
    @Override
    public void run() {
        try {
            while (true) { // allowed since it's a deamon
                Object o = ioh.receive();
                if (o instanceof ChatRoom[]) {
                    ChatRoom[] chatRooms = (ChatRoom[]) o;
                    Arrays.sort(chatRooms);
                    crlm.setChatRooms(chatRooms);
                    crm = null;
                } else if (o instanceof String[]) {
                    if (crm == null) {
                        synchronized (crmLock) {
                            try {
                                crmLock.wait();
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                    String[] users = (String[]) o;
                    Arrays.sort(users);
                    crm.setUsers(users);
                } else if (o instanceof ChatRoom) {
                    crlf.setVisible(false);
                    setChatRoomModel(new ChatRoomModel());
                    crf = new ChatRoomFrame(((ChatRoom) o).getName(), crlf, crm, ioh);
                    crf.setLocationRelativeTo(crlf);
                    crf.setVisible(true);
                } else if (o instanceof String) {
                    String[] cmd = ((String) o).split(" ", 2);
                    switch (cmd[0]) {
                        case "msg":
                            String[] msg = cmd[1].split(" ", 2);
                            crm.addMessage(msg[0], msg[1]);
                            break;
                        case "error":
                            JOptionPane.showMessageDialog(null, "ERROR: " + cmd[1], "ERROR", JOptionPane.ERROR_MESSAGE);
                            break;
                    }

                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(null, "ERROR: Connection to server lost.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(3);
        }
    }
}
