package chatterboxserver;

import io.IOHandler;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import sharedobjects.ChatRoom;

/**
 * Model of the server
 */
public class ServerModel {

    // map which contains all connected users and their IOHandler
    private final HashMap<String, IOHandler> userMap;
    // map which contains all chatroom names and ChatRoom objects linked to them
    private final HashMap<String, ChatRoom> chatRoomMap;

    /**
     * Constructor for the server model
     */
    public ServerModel() {
        userMap = new HashMap<>();
        chatRoomMap = new HashMap<>();
    }

    /**
     * Adds a user if the username does not already exist.
     *
     * @param username username of the user
     * @param ioh IOHandler of the user
     * @return whether the username is accepted and added or not
     */
    public boolean addUser(String username, IOHandler ioh) {
        synchronized (userMap) {
            if (!userMap.containsKey(username)) {
                userMap.put(username, ioh);
                return true;
            }
            return false;
        }
    }

    /**
     * Adds a chat room to chat room map if the chat room name doesn't already
     * exist
     *
     * @param crName name of the chat room
     * @param username username of the user whom created the chat room
     * @return whether the chat room is accepted and added or not
     */
    public boolean addChatRoom(String crName, String username) {
        synchronized (chatRoomMap) {
            if (!chatRoomMap.containsKey(crName)) {
                ChatRoom cr = new ChatRoom(crName);
                cr.addUser(username);
                chatRoomMap.put(crName, cr);
                return true;
            }
            return false;
        }
    }

    /**
     * Removes username from the map
     *
     * @param username username of a user
     */
    public void removeUser(String username) {
        userMap.remove(username);
    }

    /**
     * returns a collection of all chat rooms
     *
     * @return a collection of all chat rooms
     */
    public Collection<ChatRoom> getChatRooms() {
        return chatRoomMap.values();
    }

    /**
     * returns a list of users that are in a chat room
     *
     * @param crName the name of the chat room
     * @return a list of users that are in a chat room
     */
    public List<String> getChatRoomUsers(String crName) {
        return chatRoomMap.get(crName).getUserList();
    }

    /**
     * Lets a user join a chat room
     *
     * @param crName name of the chat room
     * @param username username of the user
     * @return whether the user joined the chat room successfully
     */
    public boolean joinChatRoom(String crName, String username) {
        synchronized (chatRoomMap) {
            if (chatRoomMap.containsKey(crName)) {
                chatRoomMap.get(crName).addUser(username);
                return true;
            }
            return false;
        }
    }

    /**
     * Lets a user leave a chat room
     *
     * @param crName name of the chat room
     * @param username username of the user
     * @return whether or not the room still exists after leaving
     */
    public boolean leaveChatRoom(String crName, String username) {
        synchronized (chatRoomMap) {
            ChatRoom cr;
            if ((cr = chatRoomMap.get(crName)) != null) {
                cr.removeUser(username);
                if (cr.isEmpty()) {
                    chatRoomMap.remove(crName);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the IOHandler of a user with username username
     *
     * @param username the username of the user
     * @return the IOHandler of a user with username username
     */
    public IOHandler getUserIOHandler(String username) {
        return userMap.get(username);
    }

    /**
     * Returns the chat room with name crName
     *
     * @param crName name of the chat room
     * @return the chat room with name crName
     */
    public ChatRoom getChatRoom(String crName) {
        return chatRoomMap.get(crName);
    }
}
