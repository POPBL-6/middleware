package utils;

import data.MessagePublish;

import java.util.ArrayList;

/**
 * Interface of the utils of the Middleware.
 *
 * @author urko
 */
public interface PSPortInterface {

    void connect(String address, int port);
    void disconnect();
    ArrayList<MessagePublish> subscribe(String [] topics);
    void unsubscribe(String [] topics);
    void publish(String topic, byte [] data);
    MessagePublish getLastSample(String topic);

}
