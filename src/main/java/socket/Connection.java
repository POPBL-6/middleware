package socket;

import data.Mailbox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Abstract class of Connections with sockets.
 *
 * @author urko
 */
public abstract class Connection implements ConnectionInterface {

    private final static Logger logger = LogManager.getLogger(Connection.class);
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Mailbox<byte []> inputMailbox;
    private Mailbox<byte []> outputMailbox;
    private Receiver receiver;
    private Sender sender;

    public void createMailbox() {
        inputMailbox = new Mailbox<>(10);
        outputMailbox = new Mailbox<>(10);
    }

    public void createInputOutput() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            logger.error("ERROR: Can't establish an input / output connection.", e);
        }
    }

    public void createThreads() {
        sender = new Sender(output, outputMailbox);
        receiver = new Receiver(input, inputMailbox);
        sender.start();
        receiver.start();
    }

    public void endConnection() {
        sender.kill();
        receiver.kill();
        try {
            sender.join();
            receiver.join();
        } catch (InterruptedException e) {
            logger.error("Error, thread was interrumpted while trying to end connections", e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            logger.error("Error when closing the socket", e);
            e.printStackTrace();
        }
    }

    public Mailbox<byte[]> getInputMailbox() {
        return inputMailbox;
    }

    public Mailbox<byte[]> getOutputMailbox() {
        return outputMailbox;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
