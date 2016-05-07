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

    protected String [] topics;

    public MessageSubscribe(String ... topics) {
    	setCharset(Message.DEFAULT_CHARSET);
        this.topics = topics;
    }
    
    public MessageSubscribe(byte[] origin) throws Exception {
    	int charsetLen = 0;
    	int topicLen;
    	int read = 0;
		String charset;
		ArrayList<String> topics = new ArrayList<String>();
		
		if(origin==null || origin.length<1 || origin[0]!=(this instanceof MessageUnsubscribe ? Message.MESSAGE_UNSUBSCRIBE:Message.MESSAGE_SUBSCRIBE))
			throw new IllegalArgumentException("Wrong magic number for "+this.getClass().getName());
		
		origin = ArrayUtils.subarray(origin, 1);
		
		for(int i = 0 ; i < Integer.BYTES ; i++) {
			charsetLen += (origin[i]<<(Byte.SIZE*i));
		}
		read += Integer.BYTES;
		charset = new String(ArrayUtils.subarray(origin,Integer.BYTES,charsetLen),"ASCII");
		read += charsetLen;
		while(read < origin.length) {
			topicLen = 0;
			for(int i = 0 ; i < Integer.BYTES ; i++) {
				topicLen += (origin[i + read]<<(Byte.SIZE*i));
			}
			read += Integer.BYTES;
			topics.add(new String(ArrayUtils.subarray(origin,read,topicLen),charset));
			read += topicLen;
		}
		
		setCharset(charset);
	    setTopics(topics.toArray(new String[0]));
    }

    public String [] getTopics() {
        return topics;
    }

    public void setTopics(String ... topics) {
        this.topics = topics;
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
			charsetLenBytes[i] = (byte)(charsetLen>>(Byte.SIZE*i));
		}
		byte[] header = (this instanceof MessageUnsubscribe) ? new byte[]{Message.MESSAGE_UNSUBSCRIBE} : new byte[]{Message.MESSAGE_SUBSCRIBE};
		out = ArrayUtils.concat(header, charsetLenBytes, charsetBytes);
		for(int i = 0 ; i < topics.length ; i++) {
			byte[] topicBytes = topics[i].getBytes(getCharset());
			for(int j = 0 ; j < Integer.BYTES ; j++) {
				topicLenBytes[j] = (byte)(topicBytes.length>>(Byte.SIZE*j));
			}
			out = ArrayUtils.concat(out,topicLenBytes,topicBytes);
		}
		return out;
    }
}
