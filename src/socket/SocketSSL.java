package socket;

import data.Mailbox;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will create a SSL Socket to connect the client with the middleware.
 *
 * TODO: Generate certificate with keytool. Public and private keys.
 *
 * @author urko
 */
public class SocketSSL {

    private final Logger logger = Logger.getLogger(SocketSSL.class.getName());
    private SSLSocket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Mailbox<byte []> inputMailbox;
    private Mailbox<byte []> outputMailbox;
    private Receiver receiver;
    private Sender sender;

    public SocketSSL(String address, int port) {
        inputMailbox = new Mailbox<>(10);
        outputMailbox = new Mailbox<>(10);
        createSocket(address, port);
        createInputOutput();
    }

    private void createSocket(String address, int port) {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            socket = (SSLSocket) factory.createSocket(address, port);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR: Can't create a SSL socket at port " + port + ".");
        }
    }

    private void createInputOutput() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR: Can't establish an input / output connection.");
        }
    }

    private void createThreads() {
        sender = new Sender(output, outputMailbox);
        receiver = new Receiver(input, inputMailbox);
        sender.start();
        receiver.start();
    }

    public void endThreads() {
        sender.kill();
        receiver.kill();
        try {
            sender.join();
            receiver.join();
        } catch (InterruptedException e) {
            // TODO: Logger.
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            // TODO: Logger.
            e.printStackTrace();
        }
    }

    public Mailbox<byte[]> getInputMailbox() {
        return inputMailbox;
    }

    public Mailbox<byte[]> getOutputMailbox() {
        return outputMailbox;
    }

}
