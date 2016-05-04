package data;

/**
 * Abstract class of the messages that are exchange between clients and broker.7
 *
 * @author urko
 */
public abstract class Message {

    private String charset;
    private String topic;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
