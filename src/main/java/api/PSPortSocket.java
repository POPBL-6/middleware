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
    
    private void manageMessagePublication(MessagePublication message) {
    	logger.info("MessagePublication received -> Timestamp: "+message.getTimestamp()+" || Sender: "+message.getSender()+" || Topic: "+message.getTopic());
    	lastSamples.put(message.getTopic(), message);
    	for(TopicListener listener : listeners) {
    		listener.publicationReceived(message);
    	}
	}
    
    private void manageMessagePublish(MessagePublish message) {
    	logger.warn("MessagePublish received from broker -> Topic: "+message.getTopic());
	}
    
    private void manageMessageSubscribe(MessageSubscribe message) {
    	logger.warn("MessageSubscribe received from broker");
	}
    
    private void manageMessageUnsubscribe(MessageUnsubscribe message) {
    	logger.warn("MessageUnsubscribe received from broker");
	}
    
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
				}
			}
		} catch (InterruptedException e) {
			//Interrupted
		} catch (NullPointerException e) {
			//Interrupted
		}
		connection.close();
		logger.info("Disconnected from broker");
    }

    public void disconnect() {
		connection.close();
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
            connection.writeMessage(message);
        } catch (InterruptedException e) {
            logger.error("InterruptedException caught on subscribe method", e);
        }
    }

    public void unsubscribe(String ... topics) {
        MessageUnsubscribe message = new MessageUnsubscribe(topics);
        try {
        	connection.writeMessage(message);
        } catch (InterruptedException e) {
            logger.error("InterruptedException caught on unsubscribe method", e);
        }
    }

    public void publish(MessagePublish message) {
        try {
        	connection.writeMessage(message);
        } catch (Exception e) {
            logger.error("Exception caught on publish method", e);
        }
    }

    public MessagePublication getLastSample(String topic) {
        return lastSamples.get(topic);
    }
    
    public void addTopicListener(TopicListener listener) {
    	listeners.add(listener);
    }
    
    public void removeTopicListener(TopicListener listener) {
    	listeners.remove(listener);
    }

    public Connection getConnection() {
        return connection;
    }

    protected void setConnection(Connection connection) {
        this.connection = connection;
    }
}
