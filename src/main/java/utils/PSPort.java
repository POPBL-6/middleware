package utils;

import data.*;
import socket.Connection;

import java.util.HashMap;

/**
 * Abstract class of PSPort.
 *
 * @author urko
 */
public abstract class PSPort implements PSPortInterface {

    private Connection socket;
    private Mailbox<byte []> inputMailbox;
    private Mailbox<byte []> outputMailbox;
    private HashMap<String, MessageToSubscriber> lastSamples;

    public void setCommunicationManager(Connection communicationManager) {
        setSocket(communicationManager);
    }

    public void disconnect() {
        socket.endConnection();
    }

    /**
     * Subscribe will subscribe the client to a topic an ask for the last message.
     *
     * @param topics
     * @return lastMessage
     */
    public void subscribe(String [] topics) {
        MessageSubscribe message = new MessageSubscribe(topics);
        try {
            inputMailbox.send(message.toByteArray());
            for (int i = 0; i < topics.length; i++) {
                MessageToSubscriber newMessage = new MessageToSubscriber(outputMailbox.receive());
                lastSamples.put(newMessage.getTopic(), newMessage);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(String [] topics) {
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
        } catch (InterruptedException e) {
            // TODO: Logger and interruption.
            e.printStackTrace();
        }
    }

    public MessageToSubscriber getLastSample(String topic) {
        return lastSamples.get(topic);
    }

    public Connection getSocket() {
        return socket;
    }

    public void setSocket(Connection socket) {
        this.socket = socket;
    }

    public Mailbox<byte[]> getInputMailbox() {
        return inputMailbox;
    }

    public void setInputMailbox(Mailbox<byte[]> inputMailbox) {
        this.inputMailbox = inputMailbox;
    }

    public Mailbox<byte[]> getOutputMailbox() {
        return outputMailbox;
    }

    public void setOutputMailbox(Mailbox<byte[]> outputMailbox) {
        this.outputMailbox = outputMailbox;
    }
}
