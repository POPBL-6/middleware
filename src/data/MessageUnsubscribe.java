package data;

/**
 * Created by urko on 4/05/16.
 */
public class MessageUnsubscribe extends MessageSubscribe {

    public MessageUnsubscribe(String ... topics) {
        this.setTopics(topics);
    }

    /**
     * Structure of the message: <TT><TM><CL><CHARSET>(<TL><TOPIC>)
     *
     * @return message
     */
    public byte [] toByteArray() {
        return null;
    }

}
