package tests.sockettests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Test;

import connection.SocketConnection;
import data.MessagePublish;

public class TestSocketConnection {

	private SocketConnection connection1, connection2;
	
	private ServerSocket serverSocket;
	private Socket socket1, socket2;
	private Semaphore semaphore;

	public void init() throws Exception {
		semaphore = new Semaphore(0);
		serverSocket = new ServerSocket(5434);
		
		new Thread() {
			public void run() {
				try {
					semaphore.release();
					socket2 = serverSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		semaphore.acquire();
		Thread.sleep(1000);
		socket1 = new Socket("127.0.0.1",5434);
		connection1 = new SocketConnection();
		connection2 = new SocketConnection();
		connection1.init(socket1, 10);
		connection2.init(socket2, 10);
	}
	
	@Test(timeout = 10000)
	public void testSendAndReceiveMessage() throws Exception {
		init();
		MessagePublish message1 = new MessagePublish("Topic","Data");
		connection1.writeMessage(message1);
		MessagePublish message2 = (MessagePublish) connection2.readMessage();
		assertEquals("testSendAndReceiveMessage wrong charset",message1.getCharset(),message2.getCharset());
		assertEquals("testSendAndReceiveMessage wrong topic",message1.getTopic(),message2.getTopic());
		assertArrayEquals("testSendAndReceiveMessage wrong data",message1.getData(),message2.getData());
	}
	
	@After
	public void cleanUp() throws Exception {
		connection1.close();
		connection2.close();
		serverSocket.close();
		connection1 = null;
		connection2 = null;
		socket1 = null;
		socket2 = null;
		serverSocket = null;
	}
	
}
