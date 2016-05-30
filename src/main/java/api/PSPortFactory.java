package api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

/**
 * This class allows the instantiation of PSPort implementations.
 */
public class PSPortFactory {
	/**
     * This method returns an implementation of PSPort.
     *
     * @param configuration The configuration string used to instantiate the PSPort object.
     * Example: "PSPortTCP --address 127.0.0.1 --port 5434"
     * @return The created PSPort instance.
     * @throws Throwable The cause of the InvocationTargetException thrown by the invocation of the requested constructor.
     * @throws IllegalArgumentException Thrown when the configuration format is invalid for this class.
     */
	public static PSPort getPort(String configuration) throws Throwable, IllegalArgumentException {
		PSPort port = null;
		try {
			String[] conf = configuration.trim().split("[ ]");
			if(conf[0].equals("file")) return getPort(getConfigurationFromFile(conf[1]));
			else {
				if(!conf[0].contains(".")) {
					configuration = "api." + configuration.trim();
					conf = configuration.trim().split("[ ]");
				}
				try {
					port = PSPort.class.cast(Class.forName(conf[0]).getMethod("getInstance", String.class).invoke(null, configuration));
				}catch(Exception e) {
					throw e;
				}
			}
		}catch(InvocationTargetException e) {
			throw e.getCause();
		} catch(Exception e) {
			throw new IllegalArgumentException(e.getClass().getName()+":"+e.getMessage());
		}
		return port;
	}
	
	/**
	 * Reads and returns the first line of the specified file.
	 * @param filename
	 * @return The configuration read from the file.
	 * @throws FileNotFoundException
	 */
	public static String getConfigurationFromFile(String filename) throws FileNotFoundException {
		Scanner s = new Scanner(new FileInputStream(filename));
		String config = s.nextLine();
		s.close();
		return config;
	}
}
