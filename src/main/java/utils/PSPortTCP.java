package utils;

import socket.TCPConnection;

/**
 * Created by urko on 5/05/16.
 */
public class PSPortTCP extends PSPort {

    public PSPortTCP(String address, int port) {
        setCommunicationManager(new TCPConnection(address, port));
        setInputMailbox(getSocket().getInputMailbox());
        setOutputMailbox(getSocket().getOutputMailbox());
    }

}
