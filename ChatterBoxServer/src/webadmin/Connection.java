package webadmin;

import chatterboxserver.ServerMain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Processes the http connection
 */
public class Connection implements Runnable {

    private final Socket s;
    private BufferedReader br;
    private BufferedWriter bw;
    // virtual map for actions
    private static final String actionsMap = "/actions/";

    /**
     * Constructor which initializes the streams of the connection
     *
     * @param s
     */
    public Connection(Socket s) {
        this.s = s;
        try {
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger("ChatterBoxServerLog").log(Level.INFO, ex.toString());
        }

    }

    /**
     * Starts the instance of Connection in a new thread Not using a thread pool
     * since there will not be many instances running at the same time
     */
    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * reads http input from the connection and processes it
     */
    @Override
    public void run() {
        try {
            String in;
            String get = null;
            while (!(in = br.readLine()).isEmpty()) {
                if (in.startsWith("GET")) {
                    get = in.split(" ")[1];
                }
            }
            if (get != null) {
                if (get.startsWith(actionsMap)) {
                    try {
                        doAction(get.substring(actionsMap.length()));
                    } catch (Exception ex) {
                        Logger.getLogger("ChatterBoxServerLog").log(Level.INFO, "Error executing action: " + get, ex);
                    }
                    showHomePage();
                } else {
                    showHomePage();
                }
            }
            s.close();
        } catch (IOException ex) {
            Logger.getLogger("ChatterBoxServerLog").log(Level.INFO, "Closed connection to webserver", ex);
        }
    }

    /**
     * Shows the home page.
     *
     * @throws IOException if the writing of the BufferedWriter fails
     */
    private void showHomePage() throws IOException {
        bw.write("HTTP/1.1 200 OK\n"
                + "\n");
        bw.write("<html><head><title>Home</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>");
        bw.write("Thread pool size: " + ServerMain.getNThreads());
        bw.write("<br />");
        bw.write("<form name=\"input\" action=\"/actions/threadPoolSize\" method=\"get\">"
                + "New threadpool size: <input type=\"text\" name=\"size\">"
                + "<input type=\"submit\" value=\"Submit\">"
                + "</form>");
        bw.write("</body></html>");
        bw.flush();
    }

    /**
     * Does the action requested by the http request.
     *
     * @param get String containing the action and all parameters
     * @throws Exception If there is something wrong with the get command.
     */
    private void doAction(String get) throws Exception {
        String[] cmd = get.split("\\?");
        String action = cmd[0];
        String[] params;
        HashMap<String, String> fullParams = null;
        if (cmd.length > 1) {
            fullParams = new HashMap<>();
            params = cmd[1].split("&");
            for (int i = 0; i < params.length; i++) {
                String[] temp = params[i].split("=");
                fullParams.put(temp[0], temp[1]);
            }
        }
        switch (action) {
            case "threadPoolSize":
                int size = Integer.parseInt(fullParams.get("size"));
                ServerMain.setNThreads(size);
                break;
        }
    }
}
