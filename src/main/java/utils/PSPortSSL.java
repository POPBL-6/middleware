package utils;

import socket.SSLConnection;

/**
 * Utils from the Middleware.
 *
 * @author urko
 */
public class PSPortSSL extends PSPort {

    public PSPortSSL(String address, int port) {
        setConnection(new SSLConnection(address, port));
        setInputMailbox(getConnection().getInputMailbox());
        setOutputMailbox(getConnection().getOutputMailbox());
    }

}
