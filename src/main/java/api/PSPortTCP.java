package api;

import socket.TCPConnection;

/**
 * API implementation using normal sockets.
 */
public class PSPortTCP extends PSPort {

    /**
     * This constructor creates a normal socket to connect to the server.
     *
     * @param address
     * @param port
     */
    public PSPortTCP(String address, int port) {
        setConnection(new TCPConnection(address, port));
        setInputMailbox(getConnection().getInputMailbox());
        setOutputMailbox(getConnection().getOutputMailbox());
    }

}
