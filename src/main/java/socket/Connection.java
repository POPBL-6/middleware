package socket;

import data.Mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class of Connections with sockets.
 *
 * @author urko
 */
public abstract class Connection implements ConnectionInterface {

    private final Logger logger = Logger.getLogger(SSLConnection.class.getName());
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
            logger.log(Level.SEVERE, "ERROR: Can't establish an input / output connection.");
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

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
