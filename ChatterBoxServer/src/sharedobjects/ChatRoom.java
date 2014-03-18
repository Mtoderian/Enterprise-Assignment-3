package sharedobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatRoom class which is used to store info about a chat room Instances of it
 * can be sent over a network connection.
 */
public class ChatRoom implements Serializable {

    private final String name;
    transient private List<String> userList;

    /**
     * Constructor which initializes name and the user list
     *
     * @param name
     */
    public ChatRoom(String name) {
        this.name = name;
        userList = new ArrayList<>();
    }

    /**
     * Gets the name of the chat room
     *
     * @return the name of the chat room
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a user to the chat room
     *
     * @param u username of the user
     */
    public void addUser(String u) {
        userList.add(u);
    }

    /**
     * Removes a user from the chat room
     *
     * @param u username of the user
     */
    public void removeUser(String u) {
        userList.remove(u);
    }

    /**
     * Returns whether the room is empty or not
     *
     * @return whether the room is empty or not
     */
    public boolean isEmpty() {
        return userList.isEmpty();
    }

    /**
     * Returns the user list
     *
     * @return the user list
     */
    public List<String> getUserList() {
        return userList;
    }

    /**
     * Returns the name of the chat room
     *
     * @return the name of the chat room
     */
    @Override
    public String toString() {
        return name;
    }
}
