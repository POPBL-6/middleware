package data;

/**
 * Abstract class of the messages that are exchange between clients and broker.
 *
 * @author urko
 */
public abstract class Message {

    public static final int MESSAGE_PUBLISH = 0;
    public static final int MESSAGE_TO_SUBSCRIBER = 1;
    public static final int MESSAGE_SUBSCRIBE = 2;
    public static final int MESSAGE_UNSUBSCRIBE = 3;

    private String charset = "UTF-8";

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}
