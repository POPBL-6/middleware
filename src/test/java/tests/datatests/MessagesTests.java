package tests.datatests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import data.Message;
import data.MessagePublication;
import data.MessagePublish;
import data.MessageReader;
import data.MessageSubscribe;
import data.MessageUnsubscribe;

public class MessagesTests {
	
	private MessagePublish msgPublish;
	private MessagePublication msgPublication;
	private MessageSubscribe msgSubscribe;
	private MessageUnsubscribe msgUnsubscribe;

	@Rule
	public Timeout globalTimeout = Timeout.seconds(10);

	@Before
	public void init() throws Exception {
		msgPublish = new MessagePublish("Topic","Object","ASCII");
		msgPublication = new MessagePublication(new MessagePublish("Topico",8),"Sender",7777);
		msgSubscribe = new MessageSubscribe("Topico1","Topico2","Topico3");
		msgUnsubscribe = new MessageUnsubscribe("Topico4","Topico5");
		msgUnsubscribe.setCharset("ASCII");
	}
	
	@Test
	public void testMessageFromByteArray() throws Exception {
		assertTrue(Message.fromByteArray(msgPublish.toByteArray()) instanceof MessagePublish);
		assertTrue(Message.fromByteArray(msgPublication.toByteArray()) instanceof MessagePublication);
		assertTrue(Message.fromByteArray(msgSubscribe.toByteArray()) instanceof MessageSubscribe);
		assertTrue(Message.fromByteArray(msgUnsubscribe.toByteArray()) instanceof MessageUnsubscribe);
	}
	
	@Test
	public void testMessageReader() throws Exception {
		MessageReader<Integer> reader = new MessageReader<Integer>();
		assertEquals("testMessageReader bad read",8,reader.readObject(msgPublication).intValue());
	}
	
	@Test
	public void testMessagePublishSerialization() throws Exception {
		byte[] serialized = msgPublish.toByteArray();
		MessagePublish msg2 = new MessagePublish(serialized);
		assertEquals("testMessagePublishSerialization bad topic","Topic",msg2.getTopic());
		assertEquals("testMessagePublishSerialization bad data","Object",msg2.getDataObject());
		assertEquals("testMessagePublishSerialization bad charset","ASCII",msg2.getCharset());
	}
	
	@Test
	public void testMessagePublicationSerialization() throws Exception {
		byte[] serialized = msgPublication.toByteArray();
		MessagePublication msg2 = new MessagePublication(serialized);
		assertEquals("testMessagePublicationSerialization bad topic","Topico",msg2.getTopic());
		assertEquals("testMessagePublicationSerialization bad data",8,msg2.getDataObject());
		assertEquals("testMessagePublicationSerialization bad charset",Message.DEFAULT_CHARSET,msg2.getCharset());
		assertEquals("testMessagePublicationSerialization bad sender","Sender",msg2.getSender());
		assertEquals("testMessagePublicationSerialization bad timestamp", 7777,msg2.getTimestamp());
	}
	
	@Test
	public void testMessageSubscribeSerialization() throws Exception {
		byte[] serialized = msgSubscribe.toByteArray();
		MessageSubscribe msg2 = new MessageSubscribe(serialized);
		assertEquals("testMessageSubscribeSerialization bad charset",Message.DEFAULT_CHARSET,msg2.getCharset());
		assertEquals("testMessageSubscribeSerialization bad topic1","Topico1",msg2.getTopics()[0]);
		assertEquals("testMessageSubscribeSerialization bad topic2","Topico2",msg2.getTopics()[1]);
		assertEquals("testMessageSubscribeSerialization bad topic3","Topico3",msg2.getTopics()[2]);
	}
	
	@Test
	public void testMessageUnubscribeSerialization() throws Exception {
		byte[] serialized = msgUnsubscribe.toByteArray();
		MessageUnsubscribe msg2 = new MessageUnsubscribe(serialized);
		assertEquals("testMessageUnubscribeSerialization bad charset","ASCII",msg2.getCharset());
		assertEquals("testMessageUnubscribeSerialization bad topic1","Topico4",msg2.getTopics()[0]);
		assertEquals("testMessageUnubscribeSerialization bad topic2","Topico5",msg2.getTopics()[1]);
	}

}
