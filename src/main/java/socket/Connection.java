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
    private String address;
    private int port;


    public void createMailbox() {
        inputMailbox = new Mailbox<>(10);
        outputMailbox = new Mailbox<>(10);
    }
    public void setMailboxes(Mailbox<byte []> inputMailbox, Mailbox<byte []> outputMailbox) {
        this.inputMailbox = inputMailbox;
        this.outputMailbox = outputMailbox;
    }
    public void createInputOutput() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            logger.error("ERROR: Can't establish an input / output connection.", e);
        }
    }

    public void setStreams(BufferedReader input, PrintWriter output) {
        this.input = input;
        this.output = output;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BufferedReader getInput() {
        return input;
    }

    public void setInput(BufferedReader input) {
        this.input = input;
    }

    public void setInputMailbox(Mailbox<byte[]> inputMailbox) {
        this.inputMailbox = inputMailbox;
    }

    public static Logger getLogger() {
        return logger;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public void setOutput(PrintWriter output) {
        this.output = output;
    }

    public void setOutputMailbox(Mailbox<byte[]> outputMailbox) {
        this.outputMailbox = outputMailbox;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Socket getSocket() {
        return socket;
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
