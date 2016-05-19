package data;

import java.io.IOException;

/**
 * Generic class that allows the automatic casting of the data Object within MessagePublish or MessagePublication.
 */
public class MessageReader <T> {
	/**
     * This method reads and returns the casted data Object within a MessagePublish or MessagePublication.
     *
     * @param msg The message from which the object must be read.
     * @return The casted object within the provided MessagePublish or MessagePublication.
     */
	@SuppressWarnings("unchecked")
	public T readObject(MessagePublish msg) throws ClassNotFoundException, IOException {
		return (T)msg.getDataObject();
	}
}
