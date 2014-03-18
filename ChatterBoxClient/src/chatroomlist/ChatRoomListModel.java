package chatroomlist;

import chatterboxclient.ClientModel;
import javax.swing.DefaultListModel;
import sharedobjects.ChatRoom;

/**
 * Chat room list model
 *
 * @author Administrator
 */
public class ChatRoomListModel extends ClientModel {

    private DefaultListModel<ChatRoom> crm;

    /**
     * Constructor for the chat room list model
     *
     * @param cr list model for chat rooms
     */
    public ChatRoomListModel(DefaultListModel<ChatRoom> cr) {
        this.crm = cr;
    }

    /**
     * Sets the chat rooms
     *
     * @param chatRooms array of chat rooms
     */
    public void setChatRooms(ChatRoom[] chatRooms) {
        crm.clear();
        for (ChatRoom cr : chatRooms) {
            crm.addElement(cr);
        }
    }
}
