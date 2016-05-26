package api;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import connection.SocketConnection;
import data.MessagePublication;

/**
 * API implementation using TCP sockets.
 */
public class PSPortTCP extends PSPortSocket {
	
	public static final int CONNECTION_BUFFER_SIZE = 10;
	
	Socket socket;

    /**
     * This constructor creates a TCP socket to connect to the server.
     *
     * @param address
     * @param port
     * @throws IOException 
     */
    public PSPortTCP(String address, int port) throws IOException {
    	SocketConnection connection = new SocketConnection();
    	socket = new Socket(address,port);
    	lastSamples = Collections.synchronizedMap(new HashMap<String, MessagePublication>());
    	listeners = new Vector<TopicListener>();
    	connection.init(socket, CONNECTION_BUFFER_SIZE);
    	this.connection = connection;
    	this.start();
    }
    
    /**
     * This factory method instantiates a PSPortTCP based on a configuration. This is meant to be used by PSPortFactory.
     * 
     * @param args The configuration string used to instantiate the PSPort object.
     * Example: "PSPortTCP --address 127.0.0.1 --port 5434"
     * @return The created PSPortTCP instance.
     */
    public static PSPort getInstance(String args) throws IllegalArgumentException, IOException {
		String address = SocketConnection.DEFAULT_ADDRESS;
		int port = SocketConnection.DEFAULT_PORT;
		try {
			String[] configuration = args.trim().split("[ ]");
			for(int i = 1 ; i < configuration.length ; i++) {
				switch(configuration[i]) {
				case "-p":
				case "--port":
					port = Integer.valueOf(configuration[++i]);
					break;
				case "-a":
				case "--address":
					address = configuration[++i];
					break;
				}
			}
		} catch(Exception e) {
			throw new IllegalArgumentException("Invalid PSPortTCP configuration format");
		}
		return new PSPortTCP(address,port);
	}
    
}
