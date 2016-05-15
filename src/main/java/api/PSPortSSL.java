package api;

import connection.SocketConnection;


/**
 * API implementation using SSL sockets.
 */
public class PSPortSSL extends PSPortSocket {

    /**
     * This constructor creates a SSL port to connect to the server.
     *
     * @param address
     * @param port
     */
    public PSPortSSL(String address, int port) {
        
    }
    
    public static PSPort getInstance(String args) throws IllegalArgumentException {
		String address = SocketConnection.DEFAULT_ADDRESS;
		int port = SocketConnection.DEFAULT_PORT;
		try {
			String[] configuration = args.trim().split("[ ]");
			for(int i = 1 ; i < configuration.length ; i++) {
				switch(configuration[i]) {
				case "-p":
					port = Integer.valueOf(configuration[++i]);
					break;
				case "-a":
					address = configuration[++i];
					break;
				}
			}
		} catch(Exception e) {
			throw new IllegalArgumentException("Invalid PSPortSSL configuration format");
		}
		return new PSPortSSL(address,port);
	}

}
