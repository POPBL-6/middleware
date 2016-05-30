package data;

import java.io.UnsupportedEncodingException;

/**
 * Abstract class of the messages that are exchange between clients and broker.
 */
public abstract class Message {

    public static final byte MESSAGE_PUBLISH = 0;
    public static final byte MESSAGE_PUBLICATION = 1;
    public static final byte MESSAGE_SUBSCRIBE = 2;
    public static final byte MESSAGE_UNSUBSCRIBE = 3;
    public static final String DEFAULT_CHARSET = "UTF-8";
    private String charset;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * This method creates a Message object from a byte array.
     *
     * @param origin
     * @return message The Message parsed from the provided byte[].
     */
    public static Message fromByteArray(byte[] origin) {
    	Message msg = null;
    	if ((origin != null) && (origin.length > 0)) {
    		try {
    			switch (origin[0]) {
        		case MESSAGE_PUBLISH:
        			msg = new MessagePublish(origin);
        			break;
    			case MESSAGE_PUBLICATION:
    				msg = new MessagePublication(origin);	
    			    break;
    			case MESSAGE_SUBSCRIBE:
    				msg = new MessageSubscribe(origin);	
    				break;
    			case MESSAGE_UNSUBSCRIBE:
    				msg = new MessageUnsubscribe(origin);	
    				break;
        		}
    		} catch (Exception e) {}
    	}
    	if (msg == null) {
    		throw new IllegalArgumentException("Bad data format");
    	}
    	return msg;
    }
    
    /**
     * Gets a byte[] representation of the Message so that it can be sent and stored.
     * 
     * @return The byte[] representation of the Message
     * @throws UnsupportedEncodingException
     */
    public abstract byte[] toByteArray() throws UnsupportedEncodingException;
    
    /**
     * Returns the identifier of the Message's type, declared in the data.Message class.
     * 
     * @return An Integer that identifies the Message's type.
     */
    public abstract int getMessageType();

}
