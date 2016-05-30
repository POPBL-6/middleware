package api;

import data.MessagePublication;

/**
 * Interface for receiving notifications when a MessagePublication arrives.
 * 
 * @author Jon Ayerdi
 */
public interface TopicListener {
	
	/**
	 * This method is called whenever a new Message is received
	 * in a PSPort where this TopicListener is registered.
	 * 
	 * @param message The received Message.
	 */
	void publicationReceived(MessagePublication message);
	
}
