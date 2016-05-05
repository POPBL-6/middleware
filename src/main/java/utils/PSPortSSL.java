package utils;

import data.*;
import socket.Connection;
import socket.SSLConnection;

import java.util.HashMap;

/**
 * Utils from the Middleware.
 *
 * @author urko
 */
public class PSPortSSL extends PSPort {

    public PSPortSSL(String address, int port) {
        setCommunicationManager(new SSLConnection(address, port));
        setInputMailbox(getSocket().getInputMailbox());
        setOutputMailbox(getSocket().getOutputMailbox());
    }

}
