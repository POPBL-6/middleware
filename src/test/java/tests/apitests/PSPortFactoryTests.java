package tests.apitests;

import api.PSPort;
import api.PSPortFactory;
import api.PSPortTCP;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Gorka Olalde on 1/6/16.
 */


public class PSPortFactoryTests {

    @Before
    public void before() {

    }

    @Test
    public void getTCPPortTest() throws Throwable {
        String address = "127.0.0.1";
        int port = 1234;
        String conf = "PSPortTCP --address 127.0.0.1 --port 1234";
        PSPort psPort = PSPortFactory.getPort(conf);
        assertNotNull(psPort);
        assertEquals(PSPortTCP.class, psPort.getClass());


    }

}
