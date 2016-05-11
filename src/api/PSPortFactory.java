package api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class PSPortFactory {
	public static PSPort getPort(String configuration) throws Throwable {
		PSPort port = null;
		try {
			String[] conf = configuration.trim().split("[ ]");
			if(conf[0].equals("file")) return getPort(getConfigurationFromFile(conf[0]));
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
	public static String getConfigurationFromFile(String filename) throws FileNotFoundException {
		Scanner s = new Scanner(new FileInputStream(filename));
		String config = s.nextLine();
		s.close();
		return config;
	}
}
