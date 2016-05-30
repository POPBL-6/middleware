package api;

import connection.SocketConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

/**
 * API implementation using TCP sockets.
 */
public class PSPortTCP extends PSPortSocket {

	private static final Logger logger = LogManager.getLogger(PSPortSocket.class);
	public static final int CONNECTION_BUFFER_SIZE = 10;
	
	private Socket socket;

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
    	lastSamples = Collections.synchronizedMap(new HashMap<>());
    	listeners = new Vector<>();
    	connection.init(socket, CONNECTION_BUFFER_SIZE);
    	this.connection = connection;
    	this.start();
		logger.info("TCP listener started");
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
					port = Integer.valueOf(configuration[++i]);
					break;
				case "--port":
					port = Integer.valueOf(configuration[++i]);
					break;
				case "-a":
					address = configuration[++i];
					break;
				case "--address":
					address = configuration[++i];
					break;
				default:
					logger.warn("Unexpected parameter was found in the configuration");
					break;
				}
			}
		} catch(Exception e) {
			logger.error("An error occurred when reading the configuration", e);
			throw new IllegalArgumentException("Invalid PSPortTCP configuration format");
		}
		return new PSPortTCP(address,port);
	}
    
}
