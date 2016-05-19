package connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import utils.ArrayUtils;
import data.Message;
import data.MessageSubscribe;

public class SocketConnection implements Connection {
	
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
				//TODO: Log mensaje recibido
				messagesIn.put(Message.fromByteArray(messageBytes));
			}
		}  catch(Exception e) {
			if(!isClosed()) {
				//TODO: Log
				e.printStackTrace();
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
				for(int i = 0 ; i < Integer.BYTES ; i++) {
					sendLength[i] = (byte)(send.length>>(Byte.SIZE*i));
				}
				send = ArrayUtils.concat(sendLength,send);
				out.write(send);
			}
		}  catch(Exception e) {
			//TODO: Log
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
				//TODO: Log
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
