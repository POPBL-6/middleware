package data;

/**
 * Message sent from the broker to the subscriber with the publication from the publisher.
 *
 * @author urko
 */
public class MessageToSubscriber extends MessagePublish {

    private String sender;

    public MessageToSubscriber(MessagePublish messagePublish, String sender) {
        setCharset(messagePublish.getCharset());
        setData(messagePublish.getData());
        setTopic(messagePublish.getTopic());
        this.sender = sender;
    }

    public MessageToSubscriber(byte [] data) {
        // TODO: Parse message.
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Structure of the message: <TT><TM><CL><TL><SL><CHARSET><TOPIC><SENDER><TYPE><DATA><TIMESTAMP>
     *
     * @return message
     */
    public byte [] toByteArray() {
        return null;
    }

}
