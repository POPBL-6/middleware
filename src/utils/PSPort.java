package utils;

import data.*;
import socket.SocketSSL;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utils from the Middleware.
 *
 * @author urko
 */
public class PSPort implements PSPortInterface {

    private SocketSSL socket;
    private Mailbox<byte []> inputMailbox;
    private Mailbox<byte []> outputMailbox;
    private HashMap<String, MessageToSubscriber> lastSamples;

    public PSPort(String address, int port) {
        connect(address, port);
    }

    public void connect(String address, int port) {
        socket = new SocketSSL(address, port);
        inputMailbox = socket.getInputMailbox();
        outputMailbox = socket.getOutputMailbox();
    }

    public void disconnect() {
        socket.endThreads();
    }

    /**
     * Subscribe will subscribe the client to a topic an ask for the last message.
     *
     * @param topics
     * @return lastMessage
     */
    public ArrayList<MessagePublish> subscribe(String [] topics) {
        MessageSubscribe message = new MessageSubscribe(topics);
        ArrayList<MessagePublish> array = null;
        try {
            inputMailbox.send(message.toByteArray());
            array = new ArrayList<>();
            for (int i = 0; i < topics.length; i++) {
                MessageToSubscriber newMessage = new MessageToSubscriber(outputMailbox.receive());
                array.add(newMessage);
                lastSamples.put(newMessage.getTopic(), newMessage);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return array;
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

}
