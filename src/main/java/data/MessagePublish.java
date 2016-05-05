package data;

/**
 * Message sent from the publisher to the broker with the publication.
 *
 * @author urko
 */
public class MessagePublish extends Message {

    private String topic;
    private byte [] data;

    public MessagePublish() {}

    public MessagePublish(String topic, byte [] data) {
        this.topic = topic;
        this.data = data;
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

    /**
     * Structure of the message: <TM><CL><TL><CHARSET><TOPIC><DATA>
     *
     * @return message
     */
    public byte [] toByteArray() {
        return null;
    }

}
