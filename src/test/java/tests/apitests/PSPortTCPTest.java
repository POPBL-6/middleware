package tests.apitests;

import api.PSPort;
import api.PSPortTCP;
import connection.SocketConnection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.Socket;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Created by Gorka Olalde on 31/5/16.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest( {PSPortTCP.class} )
public class PSPortTCPTest {

    PSPort psPortTCP;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);


    @Before
    public void before() throws IOException {

    }

    @Test
    public void getInstanceDefaultParams() throws Exception {
        PSPortTCP retVal = createMock(PSPortTCP.class);
        //Record
        expectNew(PSPortTCP.class, "127.0.0.1", 5434).andReturn(retVal);
        //Play
        replay(PSPortTCP.class, retVal);
        psPortTCP = PSPortTCP.getInstance("PSPortTCP");
        //Verify
        verify(retVal);
        assertNotNull(psPortTCP);
    }

    @Test
    public void getInstanceReducedParams() throws Exception {
        String params = "PSPortTCP -a 127.0.0.1 -p 1234";
        PSPortTCP retVal = createMock(PSPortTCP.class);
        //Record
        expectNew(PSPortTCP.class, "127.0.0.1", 1234).andReturn(retVal);
        //Play
        replay(PSPortTCP.class, retVal);
        psPortTCP = PSPortTCP.getInstance(params);
        //Verify
        verify(retVal);
        assertNotNull(psPortTCP);
    }

    @Test
    public void getInstanceFullParams() throws Exception {
        String params = "PSPortTCP  --address 127.0.0.1 --port 1234";
        PSPortTCP retVal = createMock(PSPortTCP.class);
        //Record
        expectNew(PSPortTCP.class, "127.0.0.1", 1234).andReturn(retVal);
        replay(PSPortTCP.class, retVal);
        psPortTCP = PSPortTCP.getInstance(params);
        //Verify
        verify(retVal);
        assertNotNull(psPortTCP);
    }

    @Test
    public void getInstanceUnexpectedArg() throws Exception {
        String params = "PSPortTCP  --address 127.0.0.1 --port 1234 --badarg test";
        PSPortTCP retVal = createMock(PSPortTCP.class);
        //Record
        expectNew(PSPortTCP.class, "127.0.0.1", 1234).andReturn(retVal);
        replay(PSPortTCP.class, retVal);
        psPortTCP = PSPortTCP.getInstance(params);
        //Verify
        verify(retVal);
        assertNotNull(psPortTCP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInstanceBadArgs() throws IOException {
        String params = "PSPortTCP -p -p -a";
        PSPortTCP.getInstance(params);
    }

    @Test
    public void testConstructor() throws Exception {
        String address = "127.0.0.1";
        int port = 1234;
        createMock(SocketConnection.class);
        createMock(Socket.class);
        SocketConnection connection = createMock(SocketConnection.class);
        Socket socketMock = createMock(Socket.class);
        //Record
        expectNew(SocketConnection.class).andReturn(connection);
        expectNew(Socket.class, address, port).andReturn(socketMock);
        connection.init(socketMock, PSPortTCP.CONNECTION_BUFFER_SIZE);
        replay(SocketConnection.class, Socket.class, connection, socketMock);
        psPortTCP = new PSPortTCP(address, port);
        verify(connection, socketMock);
    }
}
