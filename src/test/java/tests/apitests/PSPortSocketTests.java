package tests.apitests;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.replay;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import api.PSPortSocket;
import api.TopicListener;
import connection.Connection;
import connection.SocketConnection;
import data.MessagePublication;
import data.MessagePublish;
import data.MessageSubscribe;
import data.MessageUnsubscribe;

/**
 * Created by Gorka Olalde on 1/6/16.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"} )
@PrepareForTest( {PSPortSocket.class} )
public class PSPortSocketTests {

    private PSPortSocket psPortSocket;
    private Connection connection;
    private Map<String, MessagePublication> lastSamples;
    private Vector<TopicListener> listeners;
    private TopicListener topicListener;

    @Before
    public void before() {
        psPortSocket = new PSPortSocket() {
            @Override
            public void addTopicListener(TopicListener listener) {
                super.addTopicListener(listener);
            }
        };
        connection = createMock(SocketConnection.class);
        lastSamples = new HashMap<>();
        listeners = new Vector<>();
        topicListener = createMock(TopicListener.class);
        Whitebox.setInternalState(psPortSocket, "connection", connection);
        Whitebox.setInternalState(psPortSocket, "lastSamples", lastSamples);
        Whitebox.setInternalState(psPortSocket, "listeners", listeners);
    }

    @Test
    public void manageMessagePublicationTest() throws Exception {
        MessagePublication messagePublication = new MessagePublication(
                "UTF-8",
                new byte[]{},
                "testTopic",
                "sender",
                1L);
        listeners.add(topicListener);
        //Record
        topicListener.publicationReceived(messagePublication);
        //Play
        replay(topicListener);
        Whitebox.invokeMethod(psPortSocket, "manageMessagePublication", messagePublication);
        //Verify
        verify(topicListener);
        assertEquals(messagePublication, lastSamples.get("testTopic"));
    }

    @Test
    public void subscribeTest() throws InterruptedException {
        String[] topics = new String[]{"topic1", "topic2"};
        Capture<MessageSubscribe> messageSubscribeCapture = EasyMock.newCapture();
        //Record
        connection.writeMessage(capture(messageSubscribeCapture));
        //Play
        replay(connection);
        psPortSocket.subscribe(topics);
        //Verify
        verify(connection);
        assertArrayEquals(topics, messageSubscribeCapture.getValue().getTopics());
    }

    @Test
    public void subscribeInterruptedTest() throws InterruptedException {
        String[] topics = new String[]{"topic1", "topic2"};
        Capture<MessageSubscribe> messageSubscribeCapture = EasyMock.newCapture();
        //Record
        connection.writeMessage(capture(messageSubscribeCapture));
        expectLastCall().andThrow(new InterruptedException());
        //Play
        replay(connection);
        psPortSocket.subscribe(topics);
        //Verify
        verify(connection);
        assertArrayEquals(topics, messageSubscribeCapture.getValue().getTopics());
    }
    @Test
    public void unSubscribeTest() throws InterruptedException {
        String[] topics = new String[]{"topic1", "topic2"};
        Capture<MessageUnsubscribe> messageUnsubscribeCapture = EasyMock.newCapture();
        //Record
        connection.writeMessage(capture(messageUnsubscribeCapture));
        //Play
        replay(connection);
        psPortSocket.unsubscribe(topics);
        //Verify
        verify(connection);
        assertArrayEquals(topics, messageUnsubscribeCapture.getValue().getTopics());
    }

    @Test
    public void unSubscribeInterrumptedTest() throws InterruptedException {
        String[] topics = new String[]{"topic1", "topic2"};
        Capture<MessageUnsubscribe> messageUnsubscribeCapture = EasyMock.newCapture();
        //Record
        connection.writeMessage(capture(messageUnsubscribeCapture));
        expectLastCall().andThrow(new InterruptedException());
        //Play
        replay(connection);
        psPortSocket.unsubscribe(topics);
        //Verify
        verify(connection);
        assertArrayEquals(topics, messageUnsubscribeCapture.getValue().getTopics());
    }

    @Test
    public void publishTest() throws InterruptedException {
        MessagePublish messagePublish = new MessagePublish("topic", new byte[] {}, "UTF-8");
        Capture<MessagePublish> messagePublishCapture = EasyMock.newCapture();
        //Record
        connection.writeMessage(capture(messagePublishCapture));
        //Play
        replay(connection);
        psPortSocket.publish(messagePublish);
        //Verify
        verify(connection);
        assertEquals(messagePublish, messagePublishCapture.getValue());
    }

    @Test
    public void publishExceptionTest() throws InterruptedException {
        MessagePublish messagePublish = new MessagePublish("topic", new byte[] {}, "UTF-8");
        Capture<MessagePublish> messagePublishCapture = EasyMock.newCapture();
        //Record
        connection.writeMessage(capture(messagePublishCapture));
        expectLastCall().andThrow(new InterruptedException());
        //Play
        replay(connection);
        psPortSocket.publish(messagePublish);

        //Verify
        verify(connection);
        assertEquals(messagePublish, messagePublishCapture.getValue());
    }

    @Test
    public void threadRunPublicationReceived() throws InterruptedException {
        MessagePublication messagePublication = new MessagePublication("UTF-8", new byte[] {}, "topic", "sender", 1L);
        Capture<MessagePublication> messagePublicationCapture = newCapture();
        listeners.add(topicListener);
        //Record
        connection.setThreadToInterrupt(psPortSocket);
        expectLastCall();
        expect(connection.isClosed()).andReturn(false);
        expect(connection.readMessage()).andReturn(messagePublication);
        topicListener.publicationReceived(capture(messagePublicationCapture));
        expect(connection.isClosed()).andReturn(true);
        connection.close();
        //Play
        replay(connection, topicListener);
        psPortSocket.run();
        //Verify
        verify(connection, topicListener);
        assertEquals(messagePublication, lastSamples.get("topic"));
        assertEquals(messagePublication, messagePublicationCapture.getValue());
    }

    @Test
    public void threadRunPublishReceived() throws Exception {
        MessagePublish messagePublish = new MessagePublish("topic", new byte[]{}, "UTF-8");
        psPortSocket = PowerMock.createPartialMock(PSPortSocket.class, "manageMessagePublish");
        Whitebox.setInternalState(psPortSocket, "connection", connection);
        //Record
        connection.setThreadToInterrupt(psPortSocket);
        expectLastCall();
        expect(connection.isClosed()).andReturn(false);
        expect(connection.readMessage()).andReturn(messagePublish);
        Whitebox.invokeMethod(psPortSocket, "manageMessagePublish", messagePublish);
        expect(connection.isClosed()).andReturn(true);
        connection.close();
        //Play
        replay(psPortSocket, connection);
        psPortSocket.run();
        //Verify
        verify(psPortSocket, connection);
    }

    @Test
    public void threadRunSubscribeReceived() throws Exception {
        MessageSubscribe messageSubscribe = new MessageSubscribe("topic");
        psPortSocket = PowerMock.createPartialMock(PSPortSocket.class, "manageMessageSubscribe");
        Whitebox.setInternalState(psPortSocket, "connection", connection);
        //Record
        connection.setThreadToInterrupt(psPortSocket);
        expectLastCall();
        expect(connection.isClosed()).andReturn(false);
        expect(connection.readMessage()).andReturn(messageSubscribe);
        Whitebox.invokeMethod(psPortSocket, "manageMessageSubscribe", messageSubscribe);
        expect(connection.isClosed()).andReturn(true);
        connection.close();
        //Play
        replay(psPortSocket, connection);
        psPortSocket.run();
        //Verify
        verify(psPortSocket, connection);
    }

    @Test
    public void threadRunUnsubscribeReceived() throws Exception {
        MessageUnsubscribe messageUnsubscribe = new MessageUnsubscribe("topic");
        psPortSocket = PowerMock.createPartialMock(PSPortSocket.class, "manageMessageUnsubscribe");
        Whitebox.setInternalState(psPortSocket, "connection", connection);
        //Record
        connection.setThreadToInterrupt(psPortSocket);
        expectLastCall();
        expect(connection.isClosed()).andReturn(false);
        expect(connection.readMessage()).andReturn(messageUnsubscribe);
        Whitebox.invokeMethod(psPortSocket, "manageMessageUnsubscribe", messageUnsubscribe);
        expect(connection.isClosed()).andReturn(true);
        connection.close();
        //Play
        replay(psPortSocket, connection);
        psPortSocket.run();
        //Verify
        verify(psPortSocket, connection);
    }

    @Test
    public void threadRunUnrecognizedMessage() throws Exception {
        int badMsgType = 10;
        MessageSubscribe messageSubscribe = PowerMock.createPartialMock(MessageSubscribe.class, "getMessageType");
        //Record
        connection.setThreadToInterrupt(psPortSocket);
        expectLastCall();
        expect(connection.isClosed()).andReturn(false);
        expect(connection.readMessage()).andReturn(messageSubscribe);
        expect(messageSubscribe.getMessageType()).andReturn(badMsgType);
        expect(connection.isClosed()).andReturn(true);
        connection.close();
        //Play
        replay(messageSubscribe, connection);
        psPortSocket.run();
        //Verify
        verify(messageSubscribe, connection);
    }

    @Test
    public void threadRunInterrupted() throws InterruptedException {
        //Record
    	connection.setThreadToInterrupt(psPortSocket);
        expectLastCall();
        expect(connection.isClosed()).andReturn(false);
        expect(connection.readMessage()).andThrow(new InterruptedException());
        connection.close();
        //Play
        replay(connection);
        psPortSocket.run();
        //Verify
        verify(connection);
    }

    @Test
    public void threadRunNullMessage() throws InterruptedException {
        //Record
    	connection.setThreadToInterrupt(psPortSocket);
        expectLastCall();
        expect(connection.isClosed()).andReturn(false);
        expect(connection.readMessage()).andReturn(null);
        connection.close();
        //Play
        replay(connection);
        psPortSocket.run();
        //Verify
        verify(connection);
    }
}


