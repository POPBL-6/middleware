package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import data.Message;
import data.MessagePublication;
import data.MessagePublish;
import data.MessageReader;
import data.MessageSubscribe;
import data.MessageUnsubscribe;

public class MessagesTests {
	
	MessagePublish msgPublish;
	MessagePublication msgPublication;
	MessageSubscribe msgSubscribe;
	MessageUnsubscribe msgUnsubscribe;
	
	@Before
	public void init() throws Exception {
		msgPublish = new MessagePublish("Topic","Object","ASCII");
		msgPublication = new MessagePublication(new MessagePublish("Tópico",8),"Sender",7777);
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
		assertEquals("testMessagePublicationSerialization bad topic","Tópico",msg2.getTopic());
		assertEquals("testMessagePublicationSerialization bad data",8,msg2.getDataObject());
		assertEquals("testMessagePublicationSerialization bad charset",Message.DEFAULT_CHARSET,msg2.getCharset());
		assertEquals("testMessagePublicationSerialization bad sender","Sender",msg2.getSender());
		assertEquals("testMessagePublicationSerialization bad timestamp", 7777,msg2.getTimestamp());
	}

}
