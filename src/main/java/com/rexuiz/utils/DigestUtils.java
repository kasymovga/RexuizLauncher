package com.rexuiz.utils;

public class DigestUtils {

	private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

	public static String printHexBinary(byte[] data) {
		StringBuilder r = new StringBuilder(data.length * 2);
		for (byte b : data) {
			r.append(hexCode[(b >> 4) & 0xF]);
			r.append(hexCode[(b & 0xF)]);
		}
		return r.toString();
	}
}
