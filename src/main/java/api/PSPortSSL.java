package api;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import connection.SocketConnection;
import data.MessagePublication;

/**
 * API implementation using TCP sockets.
 */
public class PSPortSSL extends PSPortSocket {
	
	public static final int CONNECTION_BUFFER_SIZE = 10;
	
	Socket socket;

    /**
     * This constructor creates a SSLSocket to connect to the server.
     *
     * @param address
     * @param port
     * @throws IOException 
     * @throws CertificateException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyStoreException 
     * @throws UnrecoverableKeyException 
     * @throws KeyManagementException 
     */
    public PSPortSSL(String address, int port, String trustStore, String keyStore, String keyStorePassword, String protocol, String cipher) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
    	SocketConnection connection = new SocketConnection();
    	
    	//SSL setup
    	System.setProperty("javax.net.ssl.trustStore",trustStore);
    	KeyStore keystore = KeyStore.getInstance("JKS");
	    keystore.load(new FileInputStream(keyStore), keyStorePassword.toCharArray());
	    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	    kmf.init(keystore, keyStorePassword.toCharArray());
	    SSLContext sc = SSLContext.getInstance(protocol);
	    sc.init(kmf.getKeyManagers(), null, null);
	    SSLSocketFactory ssf = sc.getSocketFactory();
		SSLSocket s = (SSLSocket)ssf.createSocket(address, port);
		s.setEnabledCipherSuites(new String[] {cipher});
		s.setEnabledProtocols(new String[] {protocol});
	    s.setEnableSessionCreation(true);
		s.startHandshake();
		
    	socket = s;
    	lastSamples = Collections.synchronizedMap(new HashMap<String, MessagePublication>());
    	listeners = new Vector<TopicListener>();
    	connection.init(socket, CONNECTION_BUFFER_SIZE);
    	this.connection = connection;
    	this.start();
    }
    
    /**
     * This factory method instantiates a PSPortSSL based on a configuration. This is meant to be used by PSPortFactory.
     * 
     * @param args The configuration string used to instantiate the PSPort object.
     * Example: "PSPortSSL -a 127.0.0.1 -p 443 -t .truststore -k .keystore -kp password -pr TLSv1.2 -c TLS_DHE_DSS_WITH_AES_128_CBC_SHA256"
     * @return The created PSPortSSL instance.
     */
    public static PSPort getInstance(String args) throws IllegalArgumentException, IOException {
		String address = SocketConnection.DEFAULT_ADDRESS;
		int port = SocketConnection.DEFAULT_PORT;
		String trustStore = ".keystore";
		String keyStore = ".keystore";
		String keyStorePassword = "snowflake";
		String protocol = "TLSv1.2";
		String cipher = "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256";
		try {
			String[] configuration = args.trim().split("[ ]");
			for(int i = 1 ; i < configuration.length ; i++) {
				switch(configuration[i]) {
				case "-p":
				case "--port":
					port = Integer.valueOf(configuration[++i]);
					break;
				case "-a":
				case "--address":
					address = configuration[++i];
					break;
				case "-t":
				case "--truststore":
					trustStore = configuration[++i];
					break;
				case "-k":
				case "--keystore":
					keyStore = configuration[++i];
					break;
				case "-kp":
				case "--keyPass":
					keyStorePassword = configuration[++i];
					break;
				case "-pr":
				case "--protocol":
					protocol = configuration[++i];
					break;
				case "-c":
				case "--cipher":
					cipher = configuration[++i];
					break;
				default:
					break;
				}
			}
		} catch(Exception e) {
			throw new IllegalArgumentException("Invalid PSPortSSL configuration format");
		}
		try {
			return new PSPortSSL(address,port,trustStore,keyStore,keyStorePassword,protocol,cipher);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
    
}