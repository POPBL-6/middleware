package socket;

import data.Mailbox;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will create a SSL Socket to connect the client with the middleware.
 *
 * TODO: Generate certificate with keytool. Public and private keys.
 *
 * @author urko
 */
public class SSLConnection extends Connection {

    private final Logger logger = Logger.getLogger(SSLConnection.class.getName());

    public SSLConnection(String address, int port) {

    }

    public void init(String address, int port) {
        createMailbox();
        createSocket(address, port);
        createInputOutput();
        createThreads();
    }

    public void createSocket(String address, int port) {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            setSocket(factory.createSocket(address, port));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR: Can't create a SSL socket at port " + port + ".");
        }
    }

}
