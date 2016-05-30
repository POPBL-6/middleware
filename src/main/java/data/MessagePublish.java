package data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import utils.ArrayUtils;

/**
 * Message sent from the publisher to the broker with the publication.
 */
public class MessagePublish extends Message {



    public MessagePublish() {}


    /**
     * Constructor to create a Publish message from a topic, data array and the charset.
     * @param topic The topic of the message.
     * @param data The data of the message stored in an array.
     * @param charset The charset of the message fields.
     */
    public MessagePublish(String topic, byte [] data, String charset) {
        this.topic = topic;
        this.data = data;
        setCharset(charset);
    }

    /**
     * Constructor to create a publish message from the topic and the data, with the default charset.
     * @param topic The topic of the message.
     * @param data The data of the message.
     */
    public MessagePublish(String topic, byte [] data) {
    	this(topic,data,Message.DEFAULT_CHARSET);
    }

    /**
     * Constructor to create a publish message from a topic, the data stored in it's object, and the charset for the fields.
     * @param topic The topic of the message.
     * @param data The data of the message stored in it's object form.
     * @param charset The charset for the fields of the message.
     * @throws IOException Is thrown if the object can't be serialized.
     */
    public MessagePublish(String topic, Object data, String charset) throws IOException {
    	this(topic,ArrayUtils.serialize(data),charset);
    }

    /**
     * Constructor to create a publish messag from the topic and the data in it's object format.
     * @param topic The topic of the message.
     * @param data The data stored in it's object form.
     * @throws IOException Is thrown if the object can't be serialized.
     */
    public MessagePublish(String topic, Object data) throws IOException {
    	this(topic,ArrayUtils.serialize(data));
    }

    /**
     * Builds a publish message from the data received in it's plain byte form.
     * @param origin The message in the byte array.
     * @throws IllegalArgumentException Is thrown if the message type is not the correct one.
     * @throws UnsupportedEncodingException Is thrown if the selected charset is not known.
     */
    public MessagePublish(byte [] origin) throws IllegalArgumentException, UnsupportedEncodingException {
        if(origin==null || origin.length < 1 || readMessageType(origin) != Message.MESSAGE_PUBLISH) {
            throw new IllegalArgumentException("Wrong magic number for " + this.getClass().getName());
        }
        readHeader(origin);
        readData(origin);
    }

    /**
     * Get the stored data in byte array form.
     * @return The stored data.
     */
    public byte [] getData() {
        return data;
    }

    /**
     * Set the stored data directly in byte array form.
     * @param data The data to store.
     */
    public void setData(byte [] data) {
        this.data = data;
    }

    /**
     * Get the stored data in it's object form.
     * @return The stored data.
     * @throws ClassNotFoundException Is thrown if the class of the object is not recognized.
     * @throws IOException Is thrown if the object can't be deserialized.
     */
    public Object getDataObject() throws ClassNotFoundException, IOException {
        return ArrayUtils.deserialize(data);
    }

    /**
     * Get the stored object casted to a specified class.
     * @param sample The sample of the return class.
     * @param <T> The return type.
     * @return The object casted to the specified clas.
     * @throws ClassNotFoundException Thrown if the class is not recognized in the deserialization.
     * @throws IOException Thrown if an error occurs when deserializing the object.
     */
    @SuppressWarnings("unchecked")
	public <T> T getDataObject(T sample) throws ClassNotFoundException, IOException {
        return (T)ArrayUtils.deserialize(data);
    }

    /**
     * Set the stored data from it's object form.
     * @param data The data to store.
     * @throws IOException Thrown if the object is not serializable.
     */
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
