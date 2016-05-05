package data;

/**
 * Message sent to unsubscribe from topics.
 *
 * @author urko
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
