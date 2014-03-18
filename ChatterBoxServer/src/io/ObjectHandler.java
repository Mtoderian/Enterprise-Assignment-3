package io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handler for objects using object streams
 */
public class ObjectHandler implements IOHandler {

    /**
     * The socket for this correspondent
     */
    protected Socket socket;
    /**
     * Connected to socket's output stream
     */
    protected ObjectInputStream ois;
    /**
     * Connected to socket's input stream
     */
    protected ObjectOutputStream oos;

    /**
     * Initializes socket.
     *
     * @param s the socket to use
     */
    public ObjectHandler(Socket s) throws IOException {
        socket = s;
    }

    /**
     * initializes the reader and writer fields.
     */
    @Override
    public void initStreams() throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Closes socket without throwing an exception.
     */
    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ex) {
        }
    }

    /**
     * Sends a message to the server. This is a non-blocking send.
     *
     * @param msg the message to be sent
     */
    @Override
    public void send(Object msg) throws IOException {
        oos.writeObject(msg);
        oos.flush();
    }

    /**
     * Receives a message from server. This is a blocking receive.
     *
     * @return the message received.
     */
    @Override
    public Object receive() throws IOException {
        try {
            return ois.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException("Received non-existing Object.");
        }
    }
}
