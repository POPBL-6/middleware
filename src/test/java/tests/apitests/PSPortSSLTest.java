package tests.apitests;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import api.PSPort;
import api.PSPortSSL;
import connection.SocketConnection;
/**
 * Created by Gorka Olalde on 31/5/16.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest( {PSPortSSL.class, SocketConnection.class, KeyManagerFactory.class, System.class, KeyStore.class, SSLContext.class, FileInputStream.class} )
public class PSPortSSLTest {

    private PSPort psPortSSL;

    @Before
    public void before() {

    }

    @Test
    public void getInstanceDefaultParams() throws Exception {
        String address = SocketConnection.DEFAULT_ADDRESS;
        int port = SocketConnection.DEFAULT_PORT;
        String trustStore = ".keystore";
        String keyStore = ".keystore";
        String keyStorePassword = "snowflake";
        String protocol = "TLSv1.2";
        String cipher = "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256";
        PSPortSSL retVal = createMock(PSPortSSL.class);
        //Record
        expectNew(
                PSPortSSL.class,
                address,
                port,
                trustStore,
                keyStore,
                keyStorePassword,
                protocol,
                cipher
        ).andReturn(retVal);
        //Play
        replay(PSPortSSL.class, retVal);
        psPortSSL = PSPortSSL.getInstance("");
        //Verify
        verify(retVal);
        assertNotNull(psPortSSL);
    }

    @Test
    public void getInstanceReducedParams() throws Exception {
        String params = "PSPortSSL -a 127.0.0.1 -p 1234 -t test -k test -kp test -pr test -c test";
        PSPortSSL retVal = createMock(PSPortSSL.class);
        //Record
        expectNew(PSPortSSL.class, "127.0.0.1", 1234, "test", "test", "test", "test", "test").andReturn(retVal);
        //Play
        replay(PSPortSSL.class, retVal);
        psPortSSL = PSPortSSL.getInstance(params);
        //Verify
        verify(retVal);
        assertNotNull(psPortSSL);
    }

    @Test
    public void getInstanceFullParams() throws Exception {
        String params = "PSPortSSL --address 127.0.0.1 --port 1234 --truststore test --keystore test --keyPass test --protocol test --cipher test";
        PSPortSSL retVal = createMock(PSPortSSL.class);
        //Record
        expectNew(PSPortSSL.class, "127.0.0.1", 1234, "test", "test", "test", "test", "test").andReturn(retVal);
        //Play
        replay(PSPortSSL.class, retVal);
        psPortSSL = PSPortSSL.getInstance(params);
        //Verify
        verify(retVal);
        assertNotNull(psPortSSL);
    }

    @Test
    public void getInstanceUnexpectedArg() throws Exception {
        String params = "PSPortSSL  --address 127.0.0.1 --port 1234 --badarg test";
        PSPortSSL retVal = createMock(PSPortSSL.class);
        //Record
        expectNew(PSPortSSL.class, "127.0.0.1", 1234, ".keystore", ".keystore", "snowflake", "TLSv1.2", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256").andReturn(retVal);
        replay(PSPortSSL.class, retVal);
        psPortSSL = PSPortSSL.getInstance(params);
        //Verify
        verify(retVal);
        assertNotNull(psPortSSL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInstanceBadArgs() throws IOException {
        String params = "PSPortSSL -p -p -a";
        PSPortSSL.getInstance(params);
    }

    @Test
    public void testConstructor() throws Exception {
        String address = SocketConnection.DEFAULT_ADDRESS;
        int port = SocketConnection.DEFAULT_PORT;
        String trustStore = ".keystore";
        String keyStoreStr = ".keystore";
        String keyStorePassword = "snowflake";
        String protocol = "TLSv1.2";
        String cipher = "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256";
        mockStatic(System.class);
        mockStatic(KeyStore.class);
        mockStatic(KeyManagerFactory.class);
        mockStatic(SSLContext.class);

        SocketConnection connection = createMock(SocketConnection.class);
        KeyStore keyStore = createMock(KeyStore.class);
        KeyManagerFactory keyManagerFactory = createMock(KeyManagerFactory.class);
        SSLContext sslContext = createMock(SSLContext.class);
        SSLSocketFactory sslSocketFactory = createMock(SSLSocketFactory.class);
        SSLSocket sslSocket = createMock(SSLSocket.class);
        FileInputStream fileInputStream = createMock(FileInputStream.class);
        //Record
        expect(System.setProperty("javax.net.ssl.trustStore", ".keystore")).andReturn("");

        expectNew(SocketConnection.class).andReturn(connection);

        expect(KeyStore.getInstance("JKS")).andReturn(keyStore);
        keyStore.load(eq(fileInputStream), aryEq(keyStorePassword.toCharArray()));
        expectNew(FileInputStream.class, keyStoreStr).andReturn(fileInputStream);

        expect(KeyManagerFactory.getInstance("SunX509")).andReturn(keyManagerFactory);
        keyManagerFactory.init(eq(keyStore), aryEq(keyStorePassword.toCharArray()));

        expect(SSLContext.getInstance(protocol)).andReturn(sslContext);
        sslContext.init(anyObject(), eq(null), eq(null));
        expect(keyManagerFactory.getKeyManagers()).andReturn(new KeyManager[]{});
        expect(sslContext.getSocketFactory()).andReturn(sslSocketFactory);
        expect(sslSocketFactory.createSocket(address, port)).andReturn(sslSocket);

        sslSocket.setEnabledCipherSuites(aryEq(new String[]{cipher}));
        sslSocket.setEnabledProtocols(aryEq(new String[]{protocol}));
        sslSocket.setEnableSessionCreation(true);
        sslSocket.startHandshake();

        connection.init(sslSocket, 10);
        //Replay
        replay(System.class, SocketConnection.class, KeyStore.class, FileInputStream.class, KeyManagerFactory.class, SSLContext.class, connection, keyStore, keyManagerFactory, sslContext, sslSocketFactory, sslSocket);
        psPortSSL = new PSPortSSL(address, port, trustStore, keyStoreStr, keyStorePassword, protocol, cipher);
        verify(PSPortSSL.class);
        assertNotNull(psPortSSL);
    }
}
