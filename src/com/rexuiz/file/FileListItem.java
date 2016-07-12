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
	private static final int BLOCK_SIZE = 1024;

	static public boolean checkFile(String path, String hash, long size) throws FileListItemException {
		if (size != (new File(path)).length()) {
			return false;
		}
		if (hash.equals(""))
			return true;

		FileInputStream fin = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			final byte data[] = new byte[BLOCK_SIZE];
			fin = new FileInputStream(path);
			int count;
			while ((count = fin.read(data, 0, BLOCK_SIZE)) > 0) {
				md.update(data, 0, count);
			}
		} catch (Exception ex) {
			throw new FileListItemException(path + ":\n" + ex.getMessage());
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (Exception ex) {
				}
			}
		}
		byte[] digest = md.digest();
		String hashReal = DatatypeConverter.printHexBinary(digest).toLowerCase();
		if (hash.equals(hashReal)) {
			return true;
		}
		return false;
	}

	public FileListItem(String hash, long size) {
		this.hash = hash;
		this.size = size;
	}
}
