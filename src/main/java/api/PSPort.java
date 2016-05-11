package api;

import data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import socket.Connection;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Abstract class of PSPort. This class is the main part of the API.
 */
public abstract class PSPort implements PSPortInterface {

    private static final Logger logger = LogManager.getLogger(PSPort.class);

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
            /* TODO: NAIN NAIN, no bloquees esperando
            for (int i = 0; i < topics.length; i++) {
                MessagePublication newMessage = new MessagePublication(outputMailbox.receive());
                lastSamples.put(newMessage.getTopic(), newMessage);
            }
            */
        } catch (InterruptedException e) {
            logger.error("InterruptedException caught on subscribe method", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException caught on subscribe method", e);
		}
    }

    public void unsubscribe(String ... topics) {
        MessageUnsubscribe message = new MessageUnsubscribe(topics);
        try {
            inputMailbox.send(message.toByteArray());
        } catch (InterruptedException e) {
            logger.error("InterruptedException caught on unsubscribe method", e);
        } catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException caught on unsubscribe method", e);
		}
    }

    public void publish(String topic, byte [] data) {
        MessagePublish message = new MessagePublish(topic, data);
        try {
            inputMailbox.send(message.toByteArray());
        } catch (Exception e) {
            logger.error("Exception caught on publish method", e);
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
