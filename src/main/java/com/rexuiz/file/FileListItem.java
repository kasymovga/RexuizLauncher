package com.rexuiz.file;
import java.io.*;
import java.security.*;
import javax.xml.bind.*;

public class FileListItem {
	static public class FileListItemException  extends Exception {
		FileListItemException(String message) { super(message); }
	}
	final public String hash;
	final public long size;
	final public String zipSource;
	final public String zipSourceName;
	final public String zipFilePath;
	final public String zipHash;
	final public long zipSize;
	private static final int BLOCK_SIZE = 1024;

	static public boolean checkFile(String path, String hash, long size) throws FileListItemException {
		if (size != (new File(path)).length()) {
			return false;
		}

		if (hash.isEmpty()) {
			return true;
		}

		MessageDigest messageDigest;

		try (FileInputStream fin = new FileInputStream(path)){
			messageDigest = MessageDigest.getInstance("MD5");
			final byte data[] = new byte[BLOCK_SIZE];
			int count;
			while ((count = fin.read(data, 0, BLOCK_SIZE)) > 0) {
				messageDigest.update(data, 0, count);
			}
		} catch (Exception ex) {
			throw new FileListItemException(path + ":\n" + ex.getMessage());
		}

		byte[] digest = messageDigest.digest();
		String hashReal = DatatypeConverter.printHexBinary(digest).toLowerCase();

		if (hash.equals(hashReal)) {
			return true;
		}

		return false;
	}

	public FileListItem(String hash, long size) {
		this.hash = hash;
		this.size = size;
		this.zipSource = "";
		this.zipSourceName = "";
		this.zipFilePath = "";
		this.zipSize = 0;
		this.zipHash = "";
	}
	public FileListItem(String hash, long size, String zipSource, String zipSourceName, String zipFilePath, String zipHash, long zipSize) {
		this.hash = hash;
		this.size = size;
		this.zipSource = zipSource;
		this.zipSourceName = zipSourceName;
		this.zipFilePath = zipFilePath;
		this.zipHash = zipHash;
		this.zipSize = zipSize;
	}
}
