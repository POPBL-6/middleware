package data;

/**
 * Created by urko on 4/05/16.
 */
public class MessageSubscribe extends Message {

    private String [] topics;

    public MessageSubscribe(String ... topics) {
        this.topics = topics;
    }

    public String [] getTopics() {
        return topics;
    }

    public void setTopics(String [] topics) {
        this.topics = topics;
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
