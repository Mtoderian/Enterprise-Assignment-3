package chatterboxclient;

import chatroomlist.ChatRoomListFrame;
import io.IOHandler;
import io.ObjectHandler;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JOptionPane;

public class ClientMain {

    /**
     * Starts the application, creates the connection to the server and tries to
     * pick a username which is not yet taken on the server
     *
     * @param args
     */
    public static void main(String[] args) {
        String username = "";
        IOHandler ioh = null;
        Boolean accepted = false;
        String ip = JOptionPane.showInputDialog(null, "Please enter server IP", "IP", JOptionPane.QUESTION_MESSAGE);
        int port = -1;
        while(port < 0 || port > 65535){
            try{
                port = Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter server port", "Port", JOptionPane.QUESTION_MESSAGE));
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(null, "Server port should be between 0 and 65535", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
        try {
            Socket s = new Socket(ip, port);
            ioh = new ObjectHandler(s);
            ioh.initStreams();
        } catch (IOException ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(null, "ERROR: Could not connect to server!", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        while (!accepted) {
            while (username.isEmpty()) {
                username = JOptionPane.showInputDialog(null, "Please enter your username", "Username", JOptionPane.QUESTION_MESSAGE);
                if (username == null) {
                    System.exit(0);
                } else if (username.isEmpty()) {
                    JOptionPane.showInputDialog(null, "Username cannot be empty", "Username cannot be empty", JOptionPane.WARNING_MESSAGE);
                }
            }

            try {
                ioh.send(username);
                accepted = (Boolean) ioh.receive();
                if (!accepted) {
                    JOptionPane.showMessageDialog(null, "Username already in use, please pick another.", "Username already in use", JOptionPane.WARNING_MESSAGE);
                    username = "";
                }
            } catch (IOException ex) {
                System.err.println(ex);
                JOptionPane.showMessageDialog(null, "ERROR: Connection to server lost.", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(2);
            }
        }
        ChatRoomListFrame cf = new ChatRoomListFrame(username, ioh);
        cf.setLocationRelativeTo(null);
        cf.setVisible(true);
    }
}
