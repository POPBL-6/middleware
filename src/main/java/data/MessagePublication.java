package data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import utils.ArrayUtils;

/**
 * Message sent from the broker to the subscriber with the publication from the publisher.
 * Should be created by the Broker and read by the Middleware users.
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
     * Meant to be used by the MessagesManager of the Broker.
     *
     * @param messagePublish
     * @param sender
     * @param timestamp
     */
    public MessagePublication(MessagePublish messagePublish, String sender, long timestamp) {
        this(messagePublish.getCharset(), messagePublish.getData(), messagePublish.getTopic(), sender,timestamp);
    }

    /**
     * This constructor completes the message sent to the sender.
     * Used to deserialize a MessagePublication.
     * 
     * @param origin
     * @throws IllegalArgumentException
     * @throws UnsupportedEncodingException
     */
    public MessagePublication(byte [] origin) throws IllegalArgumentException, UnsupportedEncodingException {
		
		if(origin==null || origin.length<1 || origin[0]!=Message.MESSAGE_PUBLICATION) {
            throw new IllegalArgumentException("Wrong magic number for " + this.getClass().getName());
        }
		readHeader(origin);
		readData(origin);

    }

    @Override
	public void readHeader(byte[] origin) throws UnsupportedEncodingException {
		readCharset(origin);
		readLengths(origin);
		readCharset(origin);
		readTopic(origin);
		readSender(origin);
        readTimestamp(origin);
		readData(origin);
	}

    @Override
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


    /**
     * Reads the timestamp field of the message and stores it.
     * @param origin Message byte array
     */
	private void readTimestamp(byte[] origin) {
        int timestampOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength + topicLength + senderIdLength;
        
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(ArrayUtils.subarray(origin, timestampOffset, Long.BYTES));
        buffer.flip();
        
        timestamp = buffer.getLong();
	}

    /**
     * Reads the sender field of the message and stores it.
     * @param origin
     * @throws UnsupportedEncodingException
     */
	private void readSender(byte[] origin) throws UnsupportedEncodingException {
        int senderOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength + topicLength;
        sender = new String(ArrayUtils.subarray(origin, senderOffset ,senderIdLength),charset);
    }

    @Override
    protected void readData(byte[] origin) {
        int dataOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength + topicLength + senderIdLength + timestampLength;
        data = ArrayUtils.subarray(origin, dataOffset);
    }


    /**
     * Serializes this Message.
     * Structure of the message: [TM][CL][TL][SL][CHARSET][TOPIC][SENDER][TIMESTAMP][DATA]
     *
     * @return message
     */
    @Override
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
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(timestamp);
        timestampBytes = buffer.array();
		out = ArrayUtils.concat(new byte[]{Message.MESSAGE_PUBLICATION}, charsetLenBytes,topicLenBytes,
				senderLenBytes,charsetBytes,topicBytes,senderBytes,timestampBytes,getData());
		return out;
    }

    @Override
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
