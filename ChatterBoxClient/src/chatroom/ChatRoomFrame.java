package chatroom;

import chatroomlist.ChatRoomListFrame;
import chatterboxclient.Targets;
import io.IOHandler;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Frame showing chat room where you can chat with other users
 */
public class ChatRoomFrame extends JFrame implements ChangeListener {

    private JTextArea msgArea;
    private JList<String> userList;
    private JTextField inputField;
    private final IOHandler ioh;
    private final ChatRoomModel crm;

    /**
     * Constructor of ChatRoomFrame
     *
     * @param name the name of the chat room
     * @param crlf the ChatRoomListFrame which will be visible when
     * ChatRoomFrame is closed
     * @param crm model of the chat room
     * @param ioHandler IOHandler of the connection
     */
    public ChatRoomFrame(String name, final ChatRoomListFrame crlf, final ChatRoomModel crm, final IOHandler ioHandler) {
        super(name);
        this.ioh = ioHandler;
        this.crm = crm;

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        msgArea = new JTextArea();
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        userList = new JList<>(crm.getUm());
        JScrollPane jsp = new JScrollPane(userList);
        inputField = new JTextField();
        initListeners();

        cp.add(msgArea, BorderLayout.CENTER);
        cp.add(jsp, BorderLayout.EAST);
        cp.add(inputField, BorderLayout.SOUTH);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                try {
                    ioh.send("leaveChatRoom");
                } catch (IOException ex) {
                    System.err.println(ex);
                    System.exit(2);
                }
                crlf.setLocationRelativeTo(ChatRoomFrame.this);
                crlf.setVisible(true);
            }
        });

        pack();
        setBounds(0, 0, 400, 300);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Initializes all listeners of the JFrame.
     */
    private void initListeners() {
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msgText = inputField.getText();
                if (!msgText.isEmpty()) {
                    try {
                        ioh.send("msg " + msgText);
                        inputField.setText("");
                    } catch (IOException ex) {
                        System.err.println(ex);
                        System.exit(2);
                    }
                }
            }
        });
        crm.addListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(Targets.CHATMESSAGE)) {
            msgArea.append(crm.getMessage() + System.getProperty("line.separator"));
        }
    }
}
