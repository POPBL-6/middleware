package data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import utils.ArrayUtils;

/**
 * Message sent from the publisher to the broker with the publication.
 */
public class MessagePublish extends Message {



    public MessagePublish() {}
    
    public MessagePublish(String topic, byte [] data, String charset) {
        this.topic = topic;
        this.data = data;
        setCharset(charset);
    }

    public MessagePublish(String topic, byte [] data) {
    	this(topic,data,Message.DEFAULT_CHARSET);
    }
    
    public MessagePublish(String topic, Object data, String charset) throws IOException {
    	this(topic,ArrayUtils.serialize(data),charset);
    }

    public MessagePublish(String topic, Object data) throws IOException {
    	this(topic,ArrayUtils.serialize(data));
    }
    
    public MessagePublish(byte [] origin) throws IllegalArgumentException, UnsupportedEncodingException {
        if(origin==null || origin.length < 1 || readMessageType(origin) != Message.MESSAGE_PUBLISH) {
            throw new IllegalArgumentException("Wrong magic number for " + this.getClass().getName());
        }
        readHeader(origin);
        readData(origin);
    }


    public byte [] getData() {
        return data;
    }

    public void setData(byte [] data) {
        this.data = data;
    }

    public Object getDataObject() throws ClassNotFoundException, IOException {
        return ArrayUtils.deserialize(data);
    }

    @SuppressWarnings("unchecked")
	public <T> T getDataObject(T sample) throws ClassNotFoundException, IOException {
        return (T)ArrayUtils.deserialize(data);
    }

    public void setDataObject(Object data) throws IOException {
        this.data = ArrayUtils.serialize(data);
    }

    @Override
    public void readHeader(byte[] origin) throws UnsupportedEncodingException {
        readLengths(origin);
        readCharset(origin);
        readTopic(origin);
    }

    @Override
    public void readLengths(byte[] origin) {
        int charsetLengthOffset = MSG_TYPE_SIZE;
        int topicLengthOffset = Integer.BYTES + MSG_TYPE_SIZE;
        for(int i = 0 ; i < Integer.BYTES ; i++) {
            charsetLength += (origin[charsetLengthOffset + i] << (Byte.SIZE * i));
            topicLength += (origin[topicLengthOffset + i] << (Byte.SIZE * i));
        }
        lengthHeaderSize = 2 * Integer.BYTES;
    }

    @Override
    void readData(byte[] origin) {
        int dataOffset = MSG_TYPE_SIZE + lengthHeaderSize + charsetLength + topicLength;
        data = ArrayUtils.subarray(origin, dataOffset);
    }

    /**
     * Structure of the message: <TM><CL><TL><CHARSET><TOPIC><DATA>
     *
     * @return message
     * @throws Exception 
     */
    public byte [] toByteArray() throws UnsupportedEncodingException {
    	byte[] out = new byte[]{MESSAGE_PUBLISH};
		byte[] charsetBytes = getCharset().getBytes("ASCII");
		byte[] topicBytes = getTopic().getBytes(getCharset());
		int charsetLen = charsetBytes.length;
		int topicLen = topicBytes.length;
		byte[] charsetLenBytes = new byte[Integer.BYTES];
		byte[] topicLenBytes = new byte[Integer.BYTES];
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLenBytes[i] = (byte) (charsetLen >> (Byte.SIZE * i));
			topicLenBytes[i] = (byte) (topicLen >> (Byte.SIZE * i));
		}

		out = ArrayUtils.concat(out, charsetLenBytes,
				topicLenBytes,charsetBytes,topicBytes,getData());
		return out;
    }

	public int getMessageType() {
		return Message.MESSAGE_PUBLISH;
	}

}
