package api;

import data.MessagePublication;
import data.MessagePublish;

/**
 * Interface of the utils of the Middleware API.
 */
public interface PSPort {

    /**
     * This method should disconnect from the server when the API is connected.
     */
    void disconnect();

    /**
     * This method should get a array of the topics to subscribe to the server.
     * First, the method sends a request to the server to subscribe to the topics.
     * Then, the server sends the last message of that topics.
     *
     * @param topics
     */
    void subscribe(String ... topics);

    /**
     * This method should get a array of the topics to unsubscribe to the server.
     * It sends unsubscribe messages to the server, one for each topic.
     *
     * @param topics
     */
    void unsubscribe(String ... topics);

    /**
     * This method should get the topic and the data of the topic to publish it on the server.
     *
     * @param topic
     * @param data
     */
    void publish(MessagePublish publication);

    /**
     * This method returns the last message of a concrete topic.
     *
     * @param topic
     * @return MessagePublication
     */
    MessagePublication getLastSample(String topic);
    
    /**
     * This adds a listener so that it is notified of received publications.
     *
     * @param listener
     */
    void addTopicListener(TopicListener listener);
    
    /**
     * This method removes a listener so that it no longer receives notifications.
     *
     * @param listener
     */
    void removeTopicListener(TopicListener listener);
    
    /**
     * This method returns an array containing the topics which may contain data.
     *
     * @return The list of topics that contain data.
     */
    String[] getAvailableTopics();

}
