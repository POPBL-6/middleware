package data;

import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.ArrayUtils;

/**
 * Abstract class of the messages that are exchange between clients and broker.
 */
public abstract class Message {

    private static final Logger logger = LogManager.getLogger(Message.class);

    public static final byte MESSAGE_PUBLISH = 0;
    public static final byte MESSAGE_PUBLICATION = 1;
    public static final byte MESSAGE_SUBSCRIBE = 2;
    public static final byte MESSAGE_UNSUBSCRIBE = 3;
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final int MSG_TYPE_SIZE = Byte.BYTES;
    protected String charset;

	protected String topic;
	protected byte [] data;

	protected int charsetLength;
	protected int topicLength;
    protected int lengthHeaderSize;

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
    public static Message fromByteArray(byte[] origin) throws UnsupportedEncodingException, IllegalArgumentException {
    	Message msg = null;
    	if ((origin != null) && (origin.length > 0)) {
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
                default:
                    logger.error("Incorrect message type received");
                    throw new IllegalArgumentException("Incorrect message type received");
        		}
    	}
    	if (msg == null) {
    		throw new IllegalArgumentException("Bad data format");
    	}
    	return msg;
    }



    public String getTopic() {
        return topic;
    }

    /**
     * Reads the header fields of the message.
     * @param origin Message byte array.
     * @throws UnsupportedEncodingException
     */
    protected abstract void readHeader(byte[] origin) throws UnsupportedEncodingException;


    /**
     * Reads the length fields of the message.
     * @param origin Message byte array.
     */
    protected abstract void readLengths(byte[] origin);

    protected void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Reads the charset of the message and stores it to the local field.
     * @param origin Message byte array.
     * @throws UnsupportedEncodingException
     */
    protected void readCharset(byte[] origin) throws UnsupportedEncodingException {
        int charsetOffset = MSG_TYPE_SIZE + lengthHeaderSize;
        charset = new String(ArrayUtils.subarray(origin,charsetOffset ,charsetLength),"ASCII");
    }

    /**
     * Reads the topic field of the message and stores it to the local field.
     * @param origin Message byte array.
     * @throws UnsupportedEncodingException
     */
    protected void readTopic(byte[] origin) throws UnsupportedEncodingException {
        int topicOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength;
        topic = new String(ArrayUtils.subarray(origin,topicOffset, topicLength),getCharset());
    }

    /**
     * Reads the data field of the different message types.
     * @param origin Message byte array.
     */
    protected abstract void readData(byte[] origin);

    /**
     * Reads the message type field of the message.
     * @param origin Message byte array.
     * @return The message type.
     * @throws IllegalArgumentException
     */
    protected byte readMessageType(byte[] origin) throws IllegalArgumentException{
        byte type = origin[0];
        if (type == MESSAGE_PUBLICATION ||
                type == MESSAGE_PUBLISH ||
                type == MESSAGE_SUBSCRIBE ||
                type == MESSAGE_UNSUBSCRIBE) {
            return type;
        } else {
            throw new IllegalArgumentException("Wrong magic number");
        }
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
