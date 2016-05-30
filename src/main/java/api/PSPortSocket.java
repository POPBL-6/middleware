package api;

import java.util.Map;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import connection.Connection;
import data.Message;
import data.MessagePublication;
import data.MessagePublish;
import data.MessageSubscribe;
import data.MessageUnsubscribe;

/**
 * Abstract class of PSPort. This class is the main part of the API.
 */
public abstract class PSPortSocket extends Thread implements PSPort {

    private static final Logger logger = LogManager.getLogger(PSPortSocket.class);

    protected Connection connection;
    protected Map<String, MessagePublication> lastSamples;
    protected Vector<TopicListener> listeners;
    
    /**
	 * Determines what to do when a MessagePublication is received.
	 * 
	 * @param message
	 */
    private void manageMessagePublication(MessagePublication message) {
    	logger.info("MessagePublication received -> Timestamp: "+message.getTimestamp()+" || Sender: "+message.getSender()+" || Topic: "+message.getTopic());
    	lastSamples.put(message.getTopic(), message);
    	for(TopicListener listener : listeners) {
    		listener.publicationReceived(message);
    	}
	}
    
    /**
	 * Determines what to do when a MessagePublish is received.
	 * 
	 * @param message
	 */
    private void manageMessagePublish(MessagePublish message) {
    	logger.warn("MessagePublish received from broker -> Topic: "+message.getTopic());
	}
    
    /**
	 * Determines what to do when a MessageSubscribe is received.
	 * 
	 * @param message
	 */
    private void manageMessageSubscribe(MessageSubscribe message) {
    	logger.warn("MessageSubscribe received from broker");
	}
    
    /**
	 * Determines what to do when a MessageUnsubscribe is received.
	 * 
	 * @param message
	 */
    private void manageMessageUnsubscribe(MessageUnsubscribe message) {
    	logger.warn("MessageUnsubscribe received from broker");
	}
    
    /**
     * The entry point of this Thread.
     * Constantly reads and handles Messages from the Connection.
     */
    public void run() {
    	try {
			while(!connection.isClosed()) {
				Message message = connection.readMessage();
				logger.info("Message received");
				switch(message.getMessageType()) {
				case Message.MESSAGE_PUBLICATION:
					manageMessagePublication((MessagePublication) message);
					break;
				case Message.MESSAGE_PUBLISH:
					manageMessagePublish((MessagePublish) message);		
					break;
				case Message.MESSAGE_SUBSCRIBE:
					manageMessageSubscribe((MessageSubscribe) message);
					break;
				case Message.MESSAGE_UNSUBSCRIBE:
					manageMessageUnsubscribe((MessageUnsubscribe) message);
					break;
				default:
					logger.warn("Unknown message received");
					break;
				}
			}
		} catch (InterruptedException e) {
			logger.error("The receiving thread was interrupted", e);
		} catch (NullPointerException e) {
			logger.error("The message was not correctly received", e);
		}
		connection.close();
		logger.info("Disconnected from broker");
    }

    /**
     * Closes the connection.
     */
    public void disconnect() {
		connection.close();
    }

    /**
     * Subscribes this client to the specified topics by sending a MessageSubscribe.
     *
     * @param topics
     * @return lastMessage
     */
    public void subscribe(String ... topics) {
        MessageSubscribe message = new MessageSubscribe(topics);
        try {
            connection.writeMessage(message);
        } catch (InterruptedException e) {
            logger.error("InterruptedException caught on subscribe method", e);
        }
    }

    /**
     * Unsubscribes this client from the specified topics by sending a MessageUnsubscribe.
     *
     * @param topics
     * @return lastMessage
     */
    public void unsubscribe(String ... topics) {
        MessageUnsubscribe message = new MessageUnsubscribe(topics);
        try {
        	connection.writeMessage(message);
        } catch (InterruptedException e) {
            logger.error("InterruptedException caught on unsubscribe method", e);
        }
    }

    /**
     * Sends the provided MessagePublish to the Broker for distribution.
     */
    public void publish(MessagePublish message) {
        try {
        	connection.writeMessage(message);
        } catch (Exception e) {
            logger.error("Exception caught on publish method", e);
        }
    }

    /**
     * Returns the last MessagePublication received for the provided topic,
     * or null if none was received.
     */
    public MessagePublication getLastSample(String topic) {
        return lastSamples.get(topic);
    }
    
    /**
     * Registers the provided TopicListener so that it is notified
     * when a Message is received.
     */
    public void addTopicListener(TopicListener listener) {
    	listeners.add(listener);
    }
    
    /**
     * Removes a registered TopicListener, or does nothing if the
     * TopicListener was not registered.
     */
    public void removeTopicListener(TopicListener listener) {
    	listeners.remove(listener);
    }
    
    /**
     * Returns a String[] with all the topics for which
     * getLastSample(topic) will not return null.
     */
	public String[] getAvailableTopics() {
		return lastSamples.keySet().toArray(new String[0]);
	}

	/**
	 * Gets the connection used by this PSPort.
	 * 
	 * @return The connection used by this PSPort.
	 */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Sets the connection used by this PSPort.
     * @param connection The new connection to be used.
     */
    protected void setConnection(Connection connection) {
        this.connection = connection;
    }
}
