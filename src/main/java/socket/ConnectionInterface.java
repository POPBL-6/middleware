package socket;

/**
 * Interface of the Connections with sockets.
 *
 * @author urko
 */
public interface ConnectionInterface {

    void createSocket(String address, int port);
    void endConnection();
    void createInputOutput();

}
