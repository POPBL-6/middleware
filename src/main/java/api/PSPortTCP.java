package api;

import socket.TCPConnection;

/**
 * Created by urko on 5/05/16.
 */
public class PSPortTCP extends PSPort {

    public PSPortTCP(String address, int port) {
        setConnection(new TCPConnection(address, port));
        setInputMailbox(getConnection().getInputMailbox());
        setOutputMailbox(getConnection().getOutputMailbox());
    }

}
