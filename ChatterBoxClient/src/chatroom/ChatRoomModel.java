package chatroom;

import chatterboxclient.ClientModel;
import chatterboxclient.Targets;
import java.util.LinkedList;
import javax.swing.DefaultListModel;
import javax.swing.event.ChangeEvent;

/**
 * Model for a chat room
 */
public class ChatRoomModel extends ClientModel {

    private DefaultListModel<String> um;
    private LinkedList<String> messages;

    /**
     * Constructor for the chat room
     */
    public ChatRoomModel() {
        um = new DefaultListModel<>();
        messages = new LinkedList<>();
    }

    /**
     * Returns the list model containing users who are in this chat room
     *
     * @return the list model containing users who are in this chat room
     */
    public DefaultListModel<String> getUm() {
        return um;
    }

    /**
     * Sets the users who are in this chat room
     *
     * @param users the users who are in this chat room
     */
    public void setUsers(String[] users) {
        um.clear();
        for (String u : users) {
            um.addElement(u);
        }
    }

    /**
     * Adds sender and message to the linked list
     *
     * @param sender the username of the user who sent the message
     * @param message the message the user sent
     */
    public void addMessage(String sender, String message) {
        messages.push(sender + " >> " + message);
        fireStateChanged(new ChangeEvent(Targets.CHATMESSAGE));
    }

    /**
     * Retrieves and removes the fist message
     *
     * @return the fist message
     */
    public String getMessage() {
        return messages.poll();
    }
}
