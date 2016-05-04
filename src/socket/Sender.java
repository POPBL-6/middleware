package socket;

import data.Mailbox;

import java.io.PrintWriter;

/**
 * Created by urko on 4/05/16.
 */
public class Sender extends Thread {

    private PrintWriter output;
    private Mailbox<byte []> outputMailbox;
    private boolean stop;

    public Sender(PrintWriter output, Mailbox<byte []> outputMailbox) {
        this.output = output;
        this.outputMailbox = outputMailbox;
        stop = false;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                send(outputMailbox.receive());
            } catch (InterruptedException e) {
                // TODO: Logger and interruption.
                e.printStackTrace();
            }
        }
    }

    private void send(byte [] data) {
        output.print(data);
        output.flush();
    }

    public void kill() {
        stop = true;
    }

}
