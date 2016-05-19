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
import data.MessageSubscribe;

public class SocketConnection implements Connection {
	
	private static final Logger logger = LogManager.getLogger(SocketConnection.class);
	
	public static final String DEFAULT_ADDRESS = "127.0.0.1";
	public static final String DEFAULT_INTERFACE = "127.0.0.1";
	public static final int DEFAULT_PORT = 5434;
	
	private Socket socket;
	private BlockingQueue<Message> messagesIn, messagesOut;
	private Thread readingThread, writingThread;
	private volatile boolean closed;
	
	public void init(Socket socket, BlockingQueue<Message> messagesIn, BlockingQueue<Message> messagesOut) {
		closed = false;
		this.socket = socket;
		this.messagesIn = messagesIn;
		this.messagesOut = messagesOut;
		readingThread = new Thread() {
			public void run() {
				readingTask();
			}
		};
		writingThread = new Thread() {
			public void run() {
				sendingTask();
			}
		};
		readingThread.start();
		writingThread.start();
	}
	
	public void init(Socket socket, int bufferSize) {
		init(socket,new ArrayBlockingQueue<Message>(bufferSize),new ArrayBlockingQueue<Message>(bufferSize));
	}
	
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
				logger.warn("Exception thrown on receiver thread", e);
			}
		}
		close();
	}
	
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
			logger.warn("Exception thrown on sender thread", e);
			if(!isClosed()) {
				close();
			}
		}
	}

	public Message readMessage() throws InterruptedException {
		try {
			return messagesIn.take();
		} catch(Exception e) {
			return null;
		}
	}

	public void writeMessage(Message message) throws InterruptedException {
		messagesOut.put(message);
	}

	public synchronized void close() {
		try {
			if(socket!=null) {
				logger.info("Closing SocketConnection");
				closed = true;
				socket.close();
				messagesIn.offer(new MessageSubscribe());
				messagesOut.offer(new MessageSubscribe());
			}
		} catch(Exception e) {}
	}

	public synchronized boolean isClosed() {
		return closed;
	}
	
	public String toString() {
		if(socket!=null) {
			return socket.getInetAddress()+":"+socket.getPort();
		}
		else return "ConnectionClosed";
	}
	
}
