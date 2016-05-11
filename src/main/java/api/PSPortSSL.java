package api;

import socket.SSLConnection;

/**
 * API implementation using SSL sockets.
 */
public class PSPortSSL extends PSPort {

    /**
     * This constructor creates a SSL port to connect to the server.
     *
     * @param address
     * @param port
     */
    public PSPortSSL(String address, int port) {
        setConnection(new SSLConnection(address, port));
        setInputMailbox(getConnection().getInputMailbox());
        setOutputMailbox(getConnection().getOutputMailbox());
    }

}
