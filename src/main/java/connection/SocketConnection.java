package connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.ArrayUtils;
import data.Message;

/**
 * Connection implementation that uses Sockets.
 * 
 * @author Jon Ayerdi
 */
public class SocketConnection implements Connection {
	
	private static final Logger logger = LogManager.getLogger(SocketConnection.class);
	
	public static final String DEFAULT_ADDRESS = "127.0.0.1";
	public static final int DEFAULT_PORT = 5434;
	
	private Thread readingThread, writingThread;
	private Thread interruptOnClose;
	
	private Socket socket;
	private BlockingQueue<Message> messagesIn, messagesOut;
	private volatile boolean closed;
	private String id;
	
	/**
	 * Initializes this SocketConnection with the provided Socket
	 * and Messages queues.
	 * 
	 * @param socket
	 * @param messagesIn
	 * @param messagesOut
	 */
	public void init(Socket socket, BlockingQueue<Message> messagesIn, BlockingQueue<Message> messagesOut) {
		closed = false;
		this.socket = socket;
		this.messagesIn = messagesIn;
		this.messagesOut = messagesOut;
		readingThread = new Thread(() -> readingTask());
		writingThread = new Thread(() -> sendingTask());
		readingThread.start();
		writingThread.start();
	}
	
	/**
	 * Initializes this SocketConnection with the provided Socket
	 * and creates buffers of the requested length.
	 * 
	 * @param socket
	 * @param bufferSize
	 */
	public void init(Socket socket, int bufferSize) {
		init(socket, new ArrayBlockingQueue<>(bufferSize), new ArrayBlockingQueue<>(bufferSize));
	}
	
	/**
	 * Entry point of the receiving Thread.
	 * Constantly reads Messages and puts them in the messagesIn queue.
	 */
	public void readingTask() {
		try {
			InputStream in = socket.getInputStream();
			int messageLength;
			int read;
			int next;
			byte[] messageBytes;
			while(!socket.isClosed()) {
				messageLength = 0;
				read = 0;
				for(int i = 0 ; i < Integer.BYTES ; i++) {
					next = in.read();
					if(next==-1) {
						throw new SocketException("Socket closed");
					}
					messageLength += next<<(Byte.SIZE*i);
				}
				messageBytes = new byte[messageLength];
				while(read < messageLength) {
					read += in.read(messageBytes, read, messageLength-read);
				}
				messagesIn.put(Message.fromByteArray(messageBytes));
				logger.info("New message put on inbox");
			}
		}  catch(Exception e) {
			if(!isClosed()) {
				logger.warn("Exception thrown on receiver thread " + e.getClass().getName() + ": " + e.getMessage());
			}
		}
		close();
	}
	
	/**
	 * Entry point of the sending Thread.
	 * Constantly reads Messages from the messagesOut queue and sends them.
	 */
	public void sendingTask() {
		try {
			OutputStream out = socket.getOutputStream();
			byte[] send;
			byte[] sendLength = new byte[Integer.BYTES];
			while(!socket.isClosed()) {
				send = messagesOut.take().toByteArray();
				logger.info("Sending message");
				for(int i = 0 ; i < Integer.BYTES ; i++) {
					sendLength[i] = (byte)(send.length>>(Byte.SIZE*i));
				}
				send = ArrayUtils.concat(sendLength,send);
				out.write(send);
			}
		}  catch(Exception e) {
			logger.warn("Exception thrown on sender thread " + e.getClass().getName() + ": " + e.getMessage());
			if(!isClosed()) {
				close();
			}
		}
	}

	/**
	 * Reads the next Message from the messagesIn queue.
	 */
	public Message readMessage() throws InterruptedException {
		try {
			return messagesIn.take();
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Puts the Message in the messagesOut queue.
	 */
	public void writeMessage(Message message) throws InterruptedException {
		messagesOut.put(message);
	}

	/**
	 * Closes this connection, thus leaving it unusable.
	 */
	public synchronized void close() {
		try {
			if(socket!=null) {
				logger.info("Closing SocketConnection");
				closed = true;
				socket.close();
				readingThread.interrupt();
				writingThread.interrupt();
				interruptOnClose.interrupt();
			}
		} catch(Exception e) {}
	}

	/**
	 * Returns true if this Connection is not closed.
	 * 
	 * @return true if this Connection is not closed.
	 */
	public synchronized boolean isClosed() {
		return closed;
	}
	
	/**
	 * Returns a String with the InetAddress and Port of the other endpoint Socket.
	 */
	public String toString() {
		if(socket!=null) {
			return socket.getInetAddress()+":"+socket.getPort();
		}
		else {
			return "ConnectionClosed";
		}
	}

	/**
	 * Sets the Id of this connection
	 * 
	 * @param id
	 */
	public void setConnectionId(String id) {
		this.id = id;
	}

	/**
	 * Gets the Id of this connection
	 */
	public String getConnectionId() {
		return id;
	}

	/**
	 * Makes sure this the provided Thread will be interrupted
	 * when this SocketConnection is closed.
	 * 
	 * @param thread The Thread that must be interrupted
	 */
	public void setThreadToInterrupt(Thread thread) {
		interruptOnClose = thread;
	}
	
}
