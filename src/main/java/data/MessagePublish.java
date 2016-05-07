package data;

import java.io.IOException;

import utils.ArrayUtils;

/**
 * Message sent from the publisher to the broker with the publication.
 *
 * @author urko
 */
public class MessagePublish extends Message {

    private String topic;
    private byte [] data;

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
    	this(topic,ArrayUtils.serialize(data),Message.DEFAULT_CHARSET);
    }
    
    public MessagePublish(byte [] origin) throws Exception {
    	int charsetLen = 0;
		int topicLen = 0;
		String charset, topic;
		byte[] data;
		
		if(origin==null || origin.length<1 || origin[0]!=Message.MESSAGE_PUBLISH)
			throw new IllegalArgumentException("Wrong magic number for "+this.getClass().getName());
		
		origin = ArrayUtils.subarray(origin, 1);
		
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLen += (origin[i]<<(Byte.SIZE*i));
			topicLen += (origin[i+Integer.BYTES]<<(Byte.SIZE*i));
		}
		charset = new String(ArrayUtils.subarray(origin,2*Integer.BYTES,charsetLen),"ASCII");
		topic = new String(ArrayUtils.subarray(origin,2*Integer.BYTES+charsetLen,topicLen),charset);
		data = ArrayUtils.subarray(origin,2*Integer.BYTES+charsetLen+topicLen);
		
		setCharset(charset);
	    setData(data);
	    setTopic(topic);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    /**
     * Structure of the message: <TM><CL><TL><CHARSET><TOPIC><DATA>
     *
     * @return message
     * @throws Exception 
     */
    public byte [] toByteArray() throws Exception {
    	byte[] out;
		byte[] charsetBytes = getCharset().getBytes("ASCII");
		byte[] topicBytes = getTopic().getBytes(getCharset());
		int charsetLen = charsetBytes.length;
		int topicLen = topicBytes.length;
		byte[] charsetLenBytes = new byte[Integer.BYTES];
		byte[] topicLenBytes = new byte[Integer.BYTES];
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLenBytes[i] = (byte)(charsetLen>>(Byte.SIZE*i));
			topicLenBytes[i] = (byte)(topicLen>>(Byte.SIZE*i));
		}
		out = ArrayUtils.concat(new byte[]{Message.MESSAGE_PUBLISH}, charsetLenBytes,
				topicLenBytes,charsetBytes,topicBytes,getData());
		return out;
    }

}
