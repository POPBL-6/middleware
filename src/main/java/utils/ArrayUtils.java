package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ArrayUtils {
	public static byte[] toPrimitive(Byte[] in) {
		byte[] out = new byte[in.length];
		for(int i = 0 ; i < in.length ; i++)
			out[i] = in[i];
		return out;
	}
	public static byte[] subarray(byte[] in, int offset, int length) {
		byte[] out = new byte[length];
		for(int i = 0 ; i < length ; i++) {
			out[i] = in[offset+i];
		}
		return out;
	}
	public static byte[] subarray(byte[] in, int offset) {
		return subarray(in, offset, in.length-offset);
	}
	public static byte[] concat(byte[]...arrays) {
		byte[] concat;
		int finalLength = 0, contador = 0;
		for(int i = 0 ; i < arrays.length ; i++) {
			finalLength += arrays[i].length;
		}
		concat = new byte[finalLength];
		for(int i = 0 ; i < arrays.length ; i++) {
			for(int j = 0 ; j < arrays[i].length ; j++) {
				concat[contador++] = arrays[i][j];
			}
		}
		return concat;
	}
	public static boolean compare(byte[] a, byte[] b) {
		if(a.length != b.length) return false;
		for(int i = 0 ; i < a.length ; i++) {
			if(a[i] != b[i]) return false;
		}
		return true;
	}
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(obj);
		return baos.toByteArray();
	}
	public static Object deserialize(byte[] in) throws IOException, ClassNotFoundException {
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(in));
		return is.readObject();
	}
}
