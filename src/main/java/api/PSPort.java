package api;

import data.*;
import socket.Connection;

import java.util.HashMap;

/**
 * Abstract class of PSPort.
 *
 * @author urko
 */
public abstract class PSPort implements PSPortInterface {

    private Connection connection;
    private Mailbox<byte []> inputMailbox;
    private Mailbox<byte []> outputMailbox;
    private HashMap<String, MessagePublication> lastSamples;

    public void disconnect() {
        connection.endConnection();
    }

    /**
     * Subscribe will subscribe the client to a topic an ask for the last message.
     *
     * @param topics
     * @return lastMessage
     */
    public void subscribe(String ... topics) {
        MessageSubscribe message = new MessageSubscribe(topics);
        try {
            inputMailbox.send(message.toByteArray());
            /*
             * TODO: NAIN NAIN, no bloquees esperando
            for (int i = 0; i < topics.length; i++) {
                MessagePublication newMessage = new MessagePublication(outputMailbox.receive());
                lastSamples.put(newMessage.getTopic(), newMessage);
            }
            */
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(String ... topics) {
        MessageUnsubscribe message = new MessageUnsubscribe(topics);
        try {
            inputMailbox.send(message.toByteArray());
        } catch (InterruptedException e) {
            // TODO: Logger and interruption.
            e.printStackTrace();
        }
    }

    public void publish(String topic, byte [] data) {
        MessagePublish message = new MessagePublish(topic, data);
        try {
            inputMailbox.send(message.toByteArray());
        } catch (Exception e) {
            // TODO: Logger and interruption.
            e.printStackTrace();
        }
    }

    public MessagePublication getLastSample(String topic) {
        return lastSamples.get(topic);
    }

    public Connection getConnection() {
        return connection;
    }

    protected void setConnection(Connection connection) {
        this.connection = connection;
    }

    protected Mailbox<byte[]> getInputMailbox() {
        return inputMailbox;
    }

    protected void setInputMailbox(Mailbox<byte[]> inputMailbox) {
        this.inputMailbox = inputMailbox;
    }

    protected Mailbox<byte[]> getOutputMailbox() {
        return outputMailbox;
    }

    protected void setOutputMailbox(Mailbox<byte[]> outputMailbox) {
        this.outputMailbox = outputMailbox;
    }
}
