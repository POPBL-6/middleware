package socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;


/**
 * This class will create a TCP Socket to connect the client with the middleware.
 *
 * @author urko
 */
public class TCPConnection extends Connection {

    private static final Logger logger = LogManager.getLogger(SSLConnection.class);

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
            logger.error("ERROR: Can't create a SSL socket at port " + port + ".", e);
        }
    }
}
