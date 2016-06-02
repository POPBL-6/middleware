package connection;

import data.Message;

/**
 * Interface for channels from where to receive and send Messages.
 * 
 * @author Jon Ayerdi
 */
public interface Connection {
	
	/**
	 * Sets the Id of this connection
	 * 
	 * @param id
	 */
	void setConnectionId(String id);
	
	/**
	 * Gets the Id of this connection
	 */
	String getConnectionId();
	
	/**
	 * Blocks until the next Message is available.
	 * 
	 * @return The read Message.
	 * @throws InterruptedException
	 */
	Message readMessage() throws InterruptedException;
	
	/**
	 * Sends the provided Message through this channel.
	 * 
	 * @param message The Message to send.
	 * @throws InterruptedException
	 */
	void writeMessage(Message message) throws InterruptedException;
	
	/**
	 * Closes this connection, thus leaving it unusable.
	 */
	void close();
	
	/**
	 * Returns true if this Connection is not closed.
	 * 
	 * @return true if this Connection is not closed.
	 */
	boolean isClosed();
	
	/**
	 * Makes sure this the provided Thread will be interrupted
	 * when this Connection is closed.
	 * 
	 * @param thread The Thread that must be interrupted
	 */
	void setThreadToInterrupt(Thread thread);
	
}
