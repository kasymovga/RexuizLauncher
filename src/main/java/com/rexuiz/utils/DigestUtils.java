package com.rexuiz.utils;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

	public static boolean checkFile(String filePath, String hash, long size)
			throws IOException, NoSuchAlgorithmException {
		Path nioPath = Paths.get(filePath);
		long fileSize = Files.size(nioPath);

		if (fileSize != size) {
			return false;
		}

		MessageDigest messageDigest = MessageDigest.getInstance("MD5");

		try (FileChannel channel = (FileChannel) Files.newByteChannel(nioPath)) {
			MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			messageDigest.update(byteBuffer);
		} catch (IOException ex) {
			System.out.println(ex.getLocalizedMessage());
		}

		byte[] digest = messageDigest.digest();
		String hashReal = printHexBinary(digest).toLowerCase();

		return MessageDigest.isEqual(hashReal.getBytes(), hash.getBytes());
	}
}
