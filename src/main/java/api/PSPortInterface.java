package api;

import data.MessagePublication;

/**
 * Interface of the utils of the Middleware.
 *
 * @author urko
 */
public interface PSPortInterface {

    void disconnect();
    void subscribe(String ... topics);
    void unsubscribe(String ... topics);
    void publish(String topic, byte [] data);
    MessagePublication getLastSample(String topic);

}
