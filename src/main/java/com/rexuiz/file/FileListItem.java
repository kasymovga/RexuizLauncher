package com.rexuiz.file;
import com.rexuiz.utils.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileListItem {
	final public String hash;
	final public long size;
	final public String zipSource;
	final public String zipSourceName;
	final public String zipFilePath;
	final public String zipHash;
	final public long zipSize;
	private static final int BLOCK_SIZE = 1024;

	public static boolean checkFile(String path, String hash, long size) throws FileListItemException {
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
		String hashReal = DigestUtils.printHexBinary(digest).toLowerCase();

		return MessageDigest.isEqual(hashReal.getBytes(), hash.getBytes());
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
