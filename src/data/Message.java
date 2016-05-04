package data;

/**
 * Abstract class of the messages that are exchange between clients and broker.7
 *
 * @author urko
 */
public abstract class Message {

    public final int MESSAGE_PUBLISH = 0;
    public final int MESSAGE_TO_SUBSCRIBER = 1;
    public final int MESSAGE_SUBSCRIBE = 2;
    public final int MESSAGE_UNSUBSCRIBE = 3;

    private String charset = "UTF-8";

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}
