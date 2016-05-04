package utils;

import data.Message;

/**
 * Interface of the utils of the Middleware.
 *
 * @author urko
 */
public interface PSPortInterface {

    void connect(String address, int port);
    void disconnect();
    Message subscribe(String [] topic);
    void unsubscribe(String [] topic);
    void publish(String topic, byte [] data);
    Message getLastSample(String topic);

}
