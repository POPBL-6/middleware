package data;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import utils.ArrayUtils;

/**
 * Message sent to subscribe to some topics.
 *
 * @author urko
 */
public class MessageSubscribe extends Message {

    private String [] topics;
    private int topicLengthSize = Integer.BYTES;

    public MessageSubscribe(String ... topics) {
    	setCharset(Message.DEFAULT_CHARSET);
        this.topics = topics;
    }

    /**
     * Create a subscribe message from it's raw byte array form.
     * @param origin The message stored in a byte array.
     * @throws IllegalArgumentException Thrown if the message doesn't match the message types.
     * @throws UnsupportedEncodingException Thrown if the encoding is not supported by the system.
     */
    public MessageSubscribe(byte[] origin) throws IllegalArgumentException, UnsupportedEncodingException {
		
		if(origin == null ||
                origin.length < 1 ||
                origin[0] != (this instanceof MessageUnsubscribe ? Message.MESSAGE_UNSUBSCRIBE : Message.MESSAGE_SUBSCRIBE)) {
            throw new IllegalArgumentException("Wrong magic number for " + this.getClass().getName());
        }
        readHeader(origin);
        readData(origin);
        readTopics();
    }

    /**
     * Get the topics stored in the message.
     * @return The topic array.
     */
    public String [] getTopics() {
        return topics;
    }

    public void setTopics(String ... topics) {
        this.topics = topics;
    }

	@Override
	public void readHeader(byte[] origin) throws UnsupportedEncodingException {
        readLengths(origin);
        readCharset(origin);
	}

	@Override
	public void readLengths(byte[] origin) {
		int charsetLengthOffset = MSG_TYPE_SIZE;
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLength += (origin[charsetLengthOffset + i] << (Byte.SIZE * i));
		}
		lengthHeaderSize = Integer.BYTES;
	}

    /**
     *  Read the topic length of one topic stored in the data field.
     * @param origin The data field of the message.
     * @param offSet The offset where it starts to read the length.
     * @return The length of the topic.
     */
    private int readTopicLength(byte[] origin, int offSet) {
        int length = 0;
        for(int i = 0 ; i < Integer.BYTES ; i++) {
            length += (origin[offSet + i] << (Byte.SIZE * i));
        }
        return length;
    }

	@Override
	protected void readData(byte[] origin) {
        int dataOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength;
        data = ArrayUtils.subarray(origin, dataOffset);
	}

    /**
     * Reads all the topics stored in the message.
     * @throws UnsupportedEncodingException Thrown if the encoding is not supported by the system.
     */
    private void readTopics() throws UnsupportedEncodingException {
        ArrayList<String> topicList = new ArrayList<>();
        int read = 0;
        int topicLength = 0;
        String topic;
        while(read < data.length) {
            topicLength = readTopicLength(data, read);
            read += Integer.BYTES;
            topic = new String(ArrayUtils.subarray(data, read, topicLength), charset);
            topicList.add(topic);
            read += topicLength;
        }
        topics = new String[topicList.size()];
        topics = topicList.toArray(topics);
    }

	/**
     * Structure of the message: <TM><CL><CHARSET>(<TL><TOPIC>)
     *
     * @return message
     */
    public byte [] toByteArray() throws UnsupportedEncodingException {
    	byte[] out;
		byte[] charsetBytes = getCharset().getBytes("ASCII");
		int charsetLen = charsetBytes.length;
		byte[] charsetLenBytes = new byte[Integer.BYTES];
		byte[] topicLenBytes = new byte[Integer.BYTES];
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLenBytes[i] = (byte) (charsetLen >> (Byte.SIZE * i));
		}
		byte[] header = (this instanceof MessageUnsubscribe) ? new byte[]{Message.MESSAGE_UNSUBSCRIBE} : new byte[]{Message.MESSAGE_SUBSCRIBE};
		out = ArrayUtils.concat(header, charsetLenBytes, charsetBytes);
		for(int i = 0 ; i < topics.length ; i++) {
			byte[] topicBytes = topics[i].getBytes(getCharset());
			for(int j = 0 ; j < Integer.BYTES ; j++) {
				topicLenBytes[j] = (byte) (topicBytes.length >> (Byte.SIZE * j));
			}
			out = ArrayUtils.concat(out,topicLenBytes,topicBytes);
		}
		return out;
    }
    
    public int getMessageType() {
		return Message.MESSAGE_SUBSCRIBE;
	}
    
}
