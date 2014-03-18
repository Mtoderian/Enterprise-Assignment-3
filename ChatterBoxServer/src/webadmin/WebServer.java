package webadmin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Administrative web server
 */
public class WebServer implements Runnable {

    /**
     * Starts the web server
     */
    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * keeps accepting incoming connections
     */
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(8080);
            while (true) {
                Socket s = ss.accept();
                Connection c = new Connection(s);
                c.start();
            }
        } catch (IOException ex) {
            Logger.getLogger("ChatterBoxServerLog").log(Level.SEVERE, ex.toString());
        }
    }
}
