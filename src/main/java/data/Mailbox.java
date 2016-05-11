package data;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Simple generic Mailbox to share messages between threads.
 */
public class Mailbox<T> {

    private ArrayList<T> data;
    private Semaphore full;
    private Semaphore empty;
    private Semaphore lock;

    /**
     * The constructor creates a mailbox with a fixed capacity.
     *
     * @param capacity
     */
    public Mailbox(int capacity) {
        data = new ArrayList<>();
        full = new Semaphore(capacity);
        lock = new Semaphore(1);
        empty = new Semaphore(0);
    }

    /**
     * This method sends a message to the mailbox.
     * Technically, inserts a T object in a ArrayList.
     *
     * @param msg
     * @throws InterruptedException
     */
    public void send(T msg) throws InterruptedException {
        if (msg != null) {
            full.acquire();
            lock.acquire();
            data.add(msg);
            lock.release();
            empty.release();
        }
    }

    /**
     * This method receives a message from the mailbox.
     * Technically, takes out a T object from a ArrayList and returns it.
     *
     * @return message
     * @throws InterruptedException
     */
    public T receive() throws InterruptedException {
        T value;
        empty.acquire();
        lock.acquire();
        value = data.remove(0);
        lock.release();
        full.release();
        return value;
    }

    /**
     * This methods returns the number of elements in the ArrayList.
     *
     * @return numberOfMessages
     * @throws InterruptedException
     */
    public int getElementQty() throws InterruptedException {
        int numElements = 0;
        lock.acquire();
        numElements = data.size();
        lock.release();
        return numElements;
    }

}
