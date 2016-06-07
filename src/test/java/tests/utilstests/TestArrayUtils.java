package tests.utilstests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import utils.ArrayUtils;

public class TestArrayUtils {
	
	private byte[] array1 = {1,2,3,4,5}, array2 = {50,40,30,20,10};

	@Test
	public void testToPrimitive() {
		assertArrayEquals(array1,ArrayUtils.toPrimitive(new Byte[] {1,2,3,4,5}));
	}
	
	@Test
	public void testSubarray() {
		assertArrayEquals(new byte[] {3,4,5},ArrayUtils.subarray(array1, 2));
		assertArrayEquals(new byte[] {3,4},ArrayUtils.subarray(array1, 2, 2));
	}
	
	@Test
	public void testConcat() {
		assertArrayEquals(new byte[] {1,2,3,4,5,50,40,30,20,10,1,2,3,4,5},ArrayUtils.concat(array1,array2,array1));
	}
	
	@Test
	public void testSerializeDeserialize() throws ClassNotFoundException, IOException {
		String str = "HelloWorld";
		assertEquals(str,ArrayUtils.deserialize(ArrayUtils.serialize(str)).toString());
	}
	
}
