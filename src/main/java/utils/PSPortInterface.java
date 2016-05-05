package utils;

import data.MessagePublish;
import data.MessageToSubscriber;
import socket.Connection;

import java.util.ArrayList;

/**
 * Interface of the utils of the Middleware.
 *
 * @author urko
 */
public interface PSPortInterface {

    void setCommunicationManager(Connection communicationManager);
    void disconnect();
    void subscribe(String [] topics);
    void unsubscribe(String [] topics);
    void publish(String topic, byte [] data);
    MessageToSubscriber getLastSample(String topic);

}
