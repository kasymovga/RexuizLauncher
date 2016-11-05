package com.rexuiz.main;

import com.rexuiz.gui.GraphicalUserInterface;
import com.rexuiz.file.FileListItem;

import java.io.*;
import java.net.*;
import java.lang.Exception;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.HashSet;

public class Fetcher extends GraphicalUserInterface {
	private long totalSize;
	private long downloaded;
	private final int BLOCK_SIZE = 10240;
	private HashSet<String> zipFiles = new HashSet<String>();
	private int connectTimeout = 3000;
	private int readTimeout = 3000;

	public void setConnectTimeout(int timeout) {
		this.connectTimeout = timeout;
	}

	public void setReadTimeout(int timeout) {
		this.readTimeout = timeout;
	}

	public Fetcher() {
		this.setDownloadSize(0);
	}

	public void setDownloadSize(long newTotalSize) {
		downloaded = 0;
		totalSize = newTotalSize;
	}

	public class FetcherException extends Exception {
		public FetcherException(String message) { super(message); }
	}

	public boolean download(String source, String destination, String hash, long size) throws FetcherException, FileListItem.FileListItemException {
		return download(source, destination, hash, size, size);
	}

	public boolean download(String source, String destination, String hash, long size, long targetSize) throws FetcherException, FileListItem.FileListItemException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		if (hash.length() > 0 && FileListItem.checkFile(destination, hash, size)) {
			downloaded += size;
			if (totalSize > 0)
				this.progress((double)downloaded / (double)totalSize);

			return true;
		}
		try {
			long downloadedPart = 0;
			(new File(destination)).getParentFile().mkdirs();
			URL url = new URL(source);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(connectTimeout);
			con.setReadTimeout(readTimeout);
			in = new BufferedInputStream(con.getInputStream());
			fout = new FileOutputStream(destination);

			final byte data[] = new byte[BLOCK_SIZE];
			int count;
			while ((count = in.read(data, 0, BLOCK_SIZE)) > 0) {
				fout.write(data, 0, count);
				if (size > 0) {
					downloadedPart += count;
					this.subProgress((double)downloadedPart / size, "Downloading");
					this.progress((double)(downloaded + targetSize * ((double)downloadedPart / size)) / totalSize);
				}
			}
			if (hash.length() > 0) {
				if (!FileListItem.checkFile(destination, hash, size))
					return false;
			}
			if (totalSize > 0)
				downloaded += targetSize;
		} catch (Exception ex) {
			throw new Fetcher.FetcherException("Downloading failed :\n" + source + " -> " + destination + "\n" + ex.getMessage());
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
		return true;
	}
	public boolean extract(String source, String subsource, String destination, String hash, long size) throws FetcherException, FileListItem.FileListItemException {
		OutputStream out = null;
		FileInputStream fin = null;
		BufferedInputStream bin = null;
		ZipInputStream zin = null;
		try {
			out = new FileOutputStream(destination);
			fin = new FileInputStream(source);
			bin = new BufferedInputStream(fin);
			zin = new ZipInputStream(bin);
			ZipEntry ze = null;
			long extracted = 0;
			while ((ze = zin.getNextEntry()) != null)
				if (ze.getName().equals(subsource)) {
					byte[] buffer = new byte[BLOCK_SIZE];
					int len;
					while ((len = zin.read(buffer)) != -1) {
						out.write(buffer, 0, len);
						if (size > 0) {
							extracted += len;
							subProgress((double)(extracted) / size, "Extracting");
						}
					}
					break;
				}
		} catch (Exception ex) {
			throw new Fetcher.FetcherException("Unpacking failed :\n" + source + " -> " + destination + "\n" + ex.getMessage());
		} finally {
			try {
				if (out != null)
					out.close();

				if (zin != null)
					zin.close();
			} catch (Exception e) {
			}
		}
		return (hash.length() > 0 ? FileListItem.checkFile(destination, hash, size) : true);
	}
	public boolean downloadAndExtract(
			String source,
			String destination,
			String hash,
			long size,
			String zipDestination,
			String zipFilePath,
			String zipHash,
			long zipSize
	) throws FetcherException, FileListItem.FileListItemException {
		if (zipFiles.contains(zipDestination)) {
			if (totalSize > 0)
				downloaded += size;
		} else if(!download(source, zipDestination, zipHash, zipSize, size))
			return false;

		zipFiles.add(zipDestination);
		if (extract(zipDestination, zipFilePath, destination, hash, size))
			return true;

		if (totalSize > 0)
			downloaded -= size; //Extracting failed
		return false;
	}
}
