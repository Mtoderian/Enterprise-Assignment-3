package io;

import java.io.*;

/**
 * Base class for all objects that want to communicate using a socket.
 */
public interface IOHandler {

    /**
     * initializes the reader and writer fields.
     */
    public void initStreams() throws IOException;

    /**
     * Closes socket without throwing an exception.
     */
    public void close();

    /**
     * Sends a message to the server. This is a non-blocking send.
     *
     * @param msg the message to be sent
     */
    public void send(Object msg) throws IOException;

    /**
     * Receives a message from server. This is a blocking receive.
     *
     * @return the message received.
     */
    public Object receive() throws IOException;
}