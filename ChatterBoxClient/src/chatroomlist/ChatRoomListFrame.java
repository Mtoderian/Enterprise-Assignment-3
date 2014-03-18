package chatroomlist;

import io.ConnectionProcessor;
import io.IOHandler;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import sharedobjects.ChatRoom;

/**
 * Frame showing the list of chat rooms
 */
public class ChatRoomListFrame extends JFrame {

    private JButton refreshButton;
    private JList<ChatRoom> chatRooms;
    private JButton joinButton;
    private JButton createButton;
    private final IOHandler ioh;

    /**
     * Constructor for ChatRoomListFrame
     *
     * @param username username picked by the user
     * @param ioh IOHandler of the connection
     */
    public ChatRoomListFrame(String username, IOHandler ioh) {
        super("Chat Rooms");
        this.ioh = ioh;
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        DefaultListModel<ChatRoom> dlm = new DefaultListModel<>();
        ChatRoomListModel cm = new ChatRoomListModel(dlm);


        refreshButton = new JButton("REFRESH");
        chatRooms = new JList<>();
        chatRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatRooms.setModel(dlm);

        JPanel jp = new JPanel(new GridLayout(2, 1));
        joinButton = new JButton("JOIN");
        createButton = new JButton("CREATE NEW");
        initListeners();

        cp.add(refreshButton, BorderLayout.NORTH);
        cp.add(chatRooms, BorderLayout.CENTER);
        jp.add(joinButton);
        jp.add(createButton);
        cp.add(jp, BorderLayout.SOUTH);

        pack();
        setBounds(0, 0, 250, 400);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ConnectionProcessor connProc = new ConnectionProcessor(ioh, cm, this);
        connProc.start();
    }

    /**
     * Initializes all listeners of the JFrame.
     */
    private void initListeners() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ioh.send("refreshChatRoomList");
                } catch (IOException ex) {
                    System.err.println(ex);
                    System.exit(2);
                }
            }
        });

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = "";
                while (name.isEmpty()) {
                    name = JOptionPane.showInputDialog(ChatRoomListFrame.this, "Name of chatroom to create:");
                    if (name == null) {
                        return;
                    } else {
                        if (name.isEmpty()) {
                            JOptionPane.showInputDialog(null, "Name cannot be empty", "Name cannot be empty", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                try {
                    ioh.send("createChatRoom " + name);
                } catch (IOException ex) {
                    System.err.println(ex);
                    System.exit(2);
                }
            }
        });
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = chatRooms.getSelectedIndex();
                if (i >= 0) {
                    ChatRoom cr = chatRooms.getModel().getElementAt(i);
                    try {
                        ioh.send("joinChatRoom " + cr.getName());
                    } catch (IOException ex) {
                        System.err.println(ex);
                        System.exit(2);
                    }
                }
            }
        });
    }
}
