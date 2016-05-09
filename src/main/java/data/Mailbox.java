package data;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by urko on 4/05/16.
 */
public class Mailbox<T> {

    private ArrayList<T> data;
    private Semaphore lleno;
    private Semaphore vacio;
    private Semaphore lock;

    public Mailbox(int capacidad) {
        data = new ArrayList<>();
        lleno = new Semaphore(capacidad);
        lock = new Semaphore(1);
        vacio = new Semaphore(0);
    }

    public void send(T msg) throws InterruptedException {
        if (msg != null) {
            lleno.acquire();
            lock.acquire();
            data.add(msg);
            lock.release();
            vacio.release();
        }
    }

    public T receive() throws InterruptedException {
        T value;
        vacio.acquire();
        lock.acquire();
        value = data.remove(0);
        lock.release();
        lleno.release();
        return value;
    }

    public int getElementQty() throws InterruptedException {
        int numElements = 0;
        lock.acquire();
        numElements = data.size();
        lock.release();
        return numElements;
    }

}
