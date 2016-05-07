package data;

/**
 * Message sent to unsubscribe from topics.
 *
 * @author urko
 */
public class MessageUnsubscribe extends MessageSubscribe {
	public MessageUnsubscribe(String ... topics) {
        super(topics);
    }
    
    public MessageUnsubscribe(byte[] origin) throws Exception {
    	super(origin);
    }
}