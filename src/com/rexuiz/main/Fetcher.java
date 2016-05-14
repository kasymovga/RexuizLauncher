package com.rexuiz.main;

import com.rexuiz.gui.GraphicalUserInterface;
import com.rexuiz.file.FileListItem;

import java.io.*;
import java.net.*;
import java.lang.Exception;

public class Fetcher extends GraphicalUserInterface {
	private long totalSize;
	private long downloaded;
	private final int BLOCK_SIZE = 1024;

	public Fetcher() {
		this.setDownloadSize(0);
	}

	public void setDownloadSize(long newTotalSize) {
		downloaded = 0;
		totalSize = newTotalSize;
	}

	public boolean download(String source, String destination, String hash, long size) {
		boolean success = true;
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		if (hash.length() > 0 && FileListItem.checkFile(destination, hash, size)) {
			System.out.println("File " + destination + " already downloaded, skipped");
			downloaded += size;
			if (totalSize != 0)
				this.progress((double)downloaded / (double)totalSize);
			return true;
		}
		try {
			System.out.println("Source: ".concat(source));
			System.out.println("Destination: ".concat(destination));
			(new File(destination)).getParentFile().mkdirs();
			in = new BufferedInputStream(new URL(source).openStream());
			fout = new FileOutputStream(destination);

			final byte data[] = new byte[BLOCK_SIZE];
			int count;
			while ((count = in.read(data, 0, BLOCK_SIZE)) > 0) {
				downloaded += count;
				fout.write(data, 0, count);
				if (totalSize != 0)
					this.progress((double)downloaded / (double)totalSize);
			}
		} catch (Exception ex) {
			System.out.println("Download failed: " + source + " -> " + destination);
			success = false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ex) {
				}
			}
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception ex) {
				}
			}
		}
		return (hash.length() > 0 ? FileListItem.checkFile(destination, hash, size) : true);
	}
}
