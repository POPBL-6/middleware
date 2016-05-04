package utils;

import data.Message;

import java.util.HashMap;

/**
 * Utils from the Middleware.
 *
 * @author urko
 */
public class PSPort implements PSPortInterface {

    private HashMap<String, Message> lastSample;

    public PSPort() {

    }

    public void connect(String address, int port) {

    }

    public void disconnect() {

    }

    /**
     * Subscribe will subscribe the client to a topic an ask for the last message.
     *
     * @param topic
     * @return lastMessage
     */
    public Message subscribe(String [] topic) {
        return null;
    }

    public void unsubscribe(String [] topic) {

    }

    public void publish(String topic, Object data) {

    }

    public Message getLastSample(String topic) {
        return lastSample.get(topic);
    }

}
