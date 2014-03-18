package chatterboxserver;

import io.ConnectionProcessor;
import io.ObjectHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import logger.MailHandler;
import webadmin.WebServer;

/**
 * Main class of server
 */
public class ServerMain implements Runnable {

    // threadpoolexecutor which is used for the threads for incoming connections
    private static ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

    /**
     * main class which starts everything
     *
     * @param args first arg should be the port that should be used
     */
    public static void main(String[] args) {
        // creating the logger
        Logger logger = Logger.getLogger("ChatterBoxServerLog");
        try {
            FileHandler fh = new FileHandler(".\\ChatterBoxServer.log", true);
            logger.addHandler(fh);
            MailHandler mh = new MailHandler("assignment03test@gmail.com", new String[]{"assignment03test@gmail.com"}, "smtp.gmail.com", 587, "assignment03test@gmail.com", "t3st12345", true, true, false);
            logger.addHandler(mh);
            logger.setLevel(Level.ALL);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.log(Level.INFO, "Logger for the Server App is ACTIVE");

        } catch (IOException ex) {
            System.err.println("Could not create logger:\n" + ex);
        }
        // creates and starts the administration http server
        WebServer ws = new WebServer();
        ws.start();

        // creates the thread for the stdin of the server
        Thread t = new Thread(new ServerMain());
        t.start();

        // creates a new server model
        ServerModel sm = new ServerModel();
        try {
            // keep accepting newi ncoming connections
            ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
            while (true) {
                Socket s = ss.accept();
                Logger.getLogger("ChatterBoxServerLog").log(Level.INFO, "User connected");
                ObjectHandler oh = new ObjectHandler(s);
                oh.initStreams();
                ConnectionProcessor cp = new ConnectionProcessor(oh, sm);
                es.submit(cp);
            }
        } catch (IOException ex) {
            Logger.getLogger("ChatterBoxServerLog").log(Level.SEVERE, ex.toString());
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.out.println("arguments should be: <port>");
            System.exit(1);
        }
    }

    /**
     * runnable which handles the stdin
     */
    @Override
    public void run() {
        Scanner s = new Scanner(System.in);
        while (true) {
            String in = s.next();
            switch (in.toLowerCase()) {
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.out.println("> Unknown command: " + in);
                    break;
            }
        }
    }

    public static int getNThreads() {
        return es.getCorePoolSize();
    }

    public static void setNThreads(int nThreads) {
        es.setCorePoolSize(nThreads);
    }
}
