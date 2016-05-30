package data;

import java.io.UnsupportedEncodingException;

import utils.ArrayUtils;

/**
 * Message sent from the broker to the subscriber with the publication from the publisher.
 */
public class MessagePublication extends MessagePublish {

    private String sender;
    private long timestamp;
	private int senderIdLength;
    private int timestampLength = Long.BYTES;

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

    public MessagePublication(byte [] origin) throws IllegalArgumentException, UnsupportedEncodingException {
		
		if(origin==null || origin.length<1 || origin[0]!=Message.MESSAGE_PUBLICATION) {
            throw new IllegalArgumentException("Wrong magic number for " + this.getClass().getName());
        }
		readHeader(origin);
		readData(origin);

    }

	public void readHeader(byte[] origin) throws UnsupportedEncodingException {
		readCharset(origin);
		readLengths(origin);
		readCharset(origin);
		readTopic(origin);
		readSender(origin);
        readTimestamp(origin);
		readData(origin);
	}

	public void readLengths(byte[] origin) {
        final int charsetLengthOffset = MSG_TYPE_SIZE;
        final int topicLengthOffset = MSG_TYPE_SIZE + Integer.BYTES;
        final int senderLengthOffset = topicLengthOffset + Integer.BYTES;
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLength += (origin[i + charsetLengthOffset] << (Byte.SIZE * i));
			topicLength += (origin[i + topicLengthOffset] << (Byte.SIZE * i));
			senderIdLength += (origin[i + senderLengthOffset] << (Byte.SIZE * i));
		}
        lengthHeaderSize = 3 * Integer.BYTES;
	}

	private void readTimestamp(byte[] origin) {
        int timestampOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength + topicLength + senderIdLength;
		for(int i = 0 ; i < Long.BYTES ; i++) {
			timestamp += (origin[i + timestampOffset] << (Byte.SIZE * i));
		}
	}

	private void readSender(byte[] origin) throws UnsupportedEncodingException {
        int senderOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength + topicLength;
        sender = new String(ArrayUtils.subarray(origin, senderOffset ,senderIdLength),charset);
    }

    void readData(byte[] origin) {
        int dataOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength + topicLength + senderIdLength + timestampLength;
        data = ArrayUtils.subarray(origin, dataOffset);
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
			charsetLenBytes[i] = (byte)(charsetLen >> (Byte.SIZE * i));
			topicLenBytes[i] = (byte)(topicLen >> (Byte.SIZE * i));
			senderLenBytes[i] = (byte)(senderLen >> (Byte.SIZE * i));
		}
		for(int i = 0 ; i < Long.BYTES ; i++) {
			timestampBytes[i] = (byte)(timestamp >> (Byte.SIZE * i));
		}
		out = ArrayUtils.concat(new byte[]{Message.MESSAGE_PUBLICATION}, charsetLenBytes,topicLenBytes,
				senderLenBytes,charsetBytes,topicBytes,senderBytes,timestampBytes,getData());
		return out;
    }
    
    public int getMessageType() {
		return Message.MESSAGE_PUBLICATION;
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
}
