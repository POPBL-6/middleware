package tests;

import data.Mailbox;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertSame;

/**
 * Created by Gorka Olalde on 9/5/16.
 */
public class MailboxTests {

    Mailbox<Object> testSubject;
    boolean threadCompleted;
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

    @Before
    public void before() {
        testSubject = new Mailbox<>(1);
        threadCompleted = false;
    }

    @Test
    public void addElementTest() throws InterruptedException {
        Object element = new Object();
        int expectedSize = 1;
        int size;
        testSubject.send(element);
        size = testSubject.getElementQty();
        assertEquals("Expected to have 1 element", expectedSize, size);
    }

    @Test
    public void getElementTest() throws InterruptedException {
        Object element = new Object();
        Object retrievedElement;
        testSubject.send(element);
        retrievedElement = testSubject.receive();
        assertSame("Expected to return the same element", element, retrievedElement);
    }

    @Test
    public void testEmptyLocking() throws InterruptedException {
        Thread lockingThread = new Thread(() -> {
            try {
                testSubject.receive();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadCompleted = true;
        });
        lockingThread.start();
        Thread.sleep(100);
        assertFalse("Expected to lock the thread", threadCompleted);
    }

    @Test
    public void testFullLocking() throws InterruptedException {
        Object element = new Object();
        testSubject.send(element);
        Thread lockingThread = new Thread(() -> {
            try {
                testSubject.send(element);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadCompleted = true;
        });
        lockingThread.start();
        Thread.sleep(100);
        assertFalse("Expected to lock the thread", threadCompleted);
    }

    @Test
    public void testNullObjectAdd() throws InterruptedException {
        Object fakeElement = null;
        int expectedSize = 0;
        int size;
        testSubject.send(fakeElement);
        size = testSubject.getElementQty();
        assertEquals("Expected to not count as a new element", expectedSize, size);
    }

    @Test
    public void testNullObjectRetrieve() throws InterruptedException {
        Object fakeElement = null;
        testSubject.send(fakeElement);
        Thread lockingThread = new Thread(() -> {
            try {
                testSubject.receive();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadCompleted = true;
        });
        lockingThread.start();
        Thread.sleep(100);
        assertFalse("Expected to lock the thread", threadCompleted);
    }

}
