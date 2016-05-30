package data;

import java.io.UnsupportedEncodingException;

/**
 * Message sent to unsubscribe from topics.
 *
 * @author urko
 */
public class MessageUnsubscribe extends MessageSubscribe {
    /**
     * Constructor to build an unsubscribe message based on a topic array.
     * @param topics The topics to store in the message.
     */
    public MessageUnsubscribe(String ... topics) {
        super(topics);
    }

    /**
     * Constructor to build an unsubscribe message based on it's raw byte array.
     * @param origin The byte array where the message is stored.
     * @throws UnsupportedEncodingException
     */
    public MessageUnsubscribe(byte[] origin) throws UnsupportedEncodingException {
    	super(origin);
    }

    public int getMessageType() {
		return Message.MESSAGE_UNSUBSCRIBE;
	}
}