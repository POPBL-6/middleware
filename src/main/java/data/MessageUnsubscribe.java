package data;

import java.io.UnsupportedEncodingException;

/**
 * Message sent to unsubscribe from topics.
 *
 * @author urko
 */
public class MessageUnsubscribe extends MessageSubscribe {
	public MessageUnsubscribe(String ... topics) {
        super(topics);
    }
    
    public MessageUnsubscribe(byte[] origin) throws UnsupportedEncodingException {
    	super(origin);
    }
    
    public int getMessageType() {
		return Message.MESSAGE_UNSUBSCRIBE;
	}
}