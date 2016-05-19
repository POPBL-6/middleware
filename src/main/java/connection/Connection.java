package connection;

import data.Message;

public interface Connection {
	
	Message readMessage() throws InterruptedException;
	void writeMessage(Message message) throws InterruptedException;
	void close();
	boolean isClosed();
	
}
