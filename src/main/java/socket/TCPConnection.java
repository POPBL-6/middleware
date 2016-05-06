package socket;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will create a TCP Socket to connect the client with the middleware.
 *
 * @author urko
 */
public class TCPConnection extends Connection {

    private final Logger logger = Logger.getLogger(SSLConnection.class.getName());

    public TCPConnection(String address, int port) {
        init(address, port);
    }

    public void init(String address, int port) {
        createMailbox();
        createSocket(address, port);
        createInputOutput();
        createThreads();
    }

    public void createSocket(String address, int port) {
        try {
            setSocket(new Socket(address, port));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR: Can't create a SSL socket at port " + port + ".");
        }
    }
}
