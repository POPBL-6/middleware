package socket;

import data.Mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Receiver thread that will be storing all the incoming data.
 *
 * @author urko
 */
public class Receiver extends Thread {

    private BufferedReader input;
    private Mailbox<byte []> inputMailbox;
    private boolean stop;

    public Receiver(BufferedReader input, Mailbox<byte []> inputMailbox) {
        this.input = input;
        this.inputMailbox = inputMailbox;
        stop = false;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                inputMailbox.send(receive());
            } catch (InterruptedException e) {
                // TODO: Logger and interruption.
                e.printStackTrace();
            } catch (IOException e) {
                // TODO: Logger.
                e.printStackTrace();
            }
        }
    }

    private byte [] receive() throws IOException {
        // TODO: Refactor method.
        ArrayList<Byte> inputData = new ArrayList<>();
        int totalSize = input.read();
        for (int i = 0; i < totalSize; i++) {
            inputData.add((byte) input.read());
        }
        byte [] data = new byte[inputData.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = inputData.get(i);
        }
        return data;
    }

    public void kill() {
        stop = true;
    }

}
