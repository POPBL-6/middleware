package data;

import java.io.UnsupportedEncodingException;

import utils.ArrayUtils;

/**
 * Message sent from the broker to the subscriber with the publication from the publisher.
 */
public class MessagePublication extends MessagePublish {

    private String sender;
    private long timestamp;

    /**
     * This constructor creates a complete message to publish in the server.
     *
     * @param charset
     * @param data
     * @param topic
     * @param sender
     * @param timestamp
     */
    public MessagePublication(String charset, byte[] data, String topic, String sender, long timestamp) {
        setCharset(charset);
        setData(data);
        setTopic(topic);
        setSender(sender);
        setTimestamp(timestamp);
    }

    /**
     * This constructor completes the message to be sent to the sender.
     *
     * @param messagePublish
     * @param sender
     * @param timestamp
     */
    public MessagePublication(MessagePublish messagePublish, String sender, long timestamp) {
        this(messagePublish.getCharset(), messagePublish.getData(), messagePublish.getTopic(), sender,timestamp);
    }

    public MessagePublication(byte [] origin) throws Exception {
    	int charsetLen = 0;
		int topicLen = 0;
		int senderIdLen = 0;
		String charset, topic, sender;
		long timestamp = 0;
		byte[] data;
		
		if(origin==null || origin.length<1 || origin[0]!=Message.MESSAGE_PUBLICATION)
			throw new IllegalArgumentException("Wrong magic number for "+this.getClass().getName());
		
		origin = ArrayUtils.subarray(origin, 1);
		
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLen += (origin[i]<<(Byte.SIZE*i));
			topicLen += (origin[i+Integer.BYTES]<<(Byte.SIZE*i));
			senderIdLen += (origin[i+2*Integer.BYTES]<<(Byte.SIZE*i));
		}
		charset = new String(ArrayUtils.subarray(origin,3*Integer.BYTES,charsetLen),"ASCII");
		topic = new String(ArrayUtils.subarray(origin,3*Integer.BYTES+charsetLen,topicLen),charset);
		sender = new String(ArrayUtils.subarray(origin,3*Integer.BYTES+charsetLen+topicLen,senderIdLen),charset);
		timestamp = 0;
		for(int i = 0 ; i < Long.BYTES ; i++) {
			timestamp += (origin[i+3*Integer.BYTES+charsetLen+topicLen+senderIdLen]<<(Byte.SIZE*i));
		}
		data = ArrayUtils.subarray(origin,3*Integer.BYTES+Long.BYTES+charsetLen+topicLen+senderIdLen);
		
		setCharset(charset);
	    setData(data);
	    setTopic(topic);
	    setSender(sender);
	    setTimestamp(timestamp);
    }

    public String getSender() {
        return sender;
    }
    
    protected void setSender(String sender) {
		this.sender = sender;
	}
    
    public long getTimestamp() {
		return timestamp;
	}

    protected void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

    /**
     * Structure of the message: <TM><CL><TL><SL><CHARSET><TOPIC><SENDER><TIMESTAMP><DATA>
     *
     * @return message
     */
    public byte [] toByteArray() throws UnsupportedEncodingException {
    	byte[] out;
		byte[] charsetBytes = getCharset().getBytes("ASCII");
		byte[] topicBytes = getTopic().getBytes(getCharset());
		byte[] senderBytes = getSender().getBytes(getCharset());
		byte[] timestampBytes = new byte[Long.BYTES];
		int charsetLen = charsetBytes.length;
		int topicLen = topicBytes.length;
		int senderLen = senderBytes.length;
		byte[] charsetLenBytes = new byte[Integer.BYTES];
		byte[] topicLenBytes = new byte[Integer.BYTES];
		byte[] senderLenBytes = new byte[Integer.BYTES];
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLenBytes[i] = (byte)(charsetLen>>(Byte.SIZE*i));
			topicLenBytes[i] = (byte)(topicLen>>(Byte.SIZE*i));
			senderLenBytes[i] = (byte)(senderLen>>(Byte.SIZE*i));
		}
		for(int i = 0 ; i < Long.BYTES ; i++) {
			timestampBytes[i] = (byte)(timestamp>>(Byte.SIZE*i));
		}
		out = ArrayUtils.concat(new byte[]{Message.MESSAGE_PUBLICATION}, charsetLenBytes,topicLenBytes,
				senderLenBytes,charsetBytes,topicBytes,senderBytes,timestampBytes,getData());
		return out;
    }
    
    public int getMessageType() {
		return Message.MESSAGE_PUBLICATION;
	}

}
