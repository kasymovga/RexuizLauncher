package com.rexuiz.main;

import com.rexuiz.file.FileListItemException;
import com.rexuiz.gui.GraphicalUserInterface;
import com.rexuiz.file.FileListItem;

import java.io.*;
import java.net.*;
import java.lang.Exception;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.HashSet;
import java.util.Iterator;

public class Fetcher {
	private long totalSize;
	private long downloaded;
	private final int BLOCK_SIZE = 10240;
	private HashSet<String> zipFiles = new HashSet<>();
	private int connectTimeout = 3000;
	private int readTimeout = 3000;
	private GraphicalUserInterface gui;

	public void setGUI(GraphicalUserInterface gui) {
		this.gui = gui;
	}

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

	public boolean download(String source, String destination, String hash, long size) throws FetcherException,
			FileListItemException {
		return download(source, destination, hash, size, size);
	}

	private boolean download(String source, String destination, String hash, long size, long targetSize)
			throws FetcherException, FileListItemException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		if (hash.length() > 0 && FileListItem.checkFile(destination, hash, size)) {
			downloaded += targetSize;
			if (totalSize > 0)
				if (gui != null)
					gui.progress((double)downloaded / (double)totalSize);

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
					if (gui != null) {
						gui.subProgress((double)downloadedPart / size, "Downloading");
						gui.progress((downloaded + targetSize * ((double)downloadedPart / size)) / totalSize);
					}
				}
			}
			if (!hash.isEmpty()) {
				if (!FileListItem.checkFile(destination, hash, size))
					return false;
			}
			if (totalSize > 0)
				downloaded += targetSize;
		} catch (Exception ex) {
			throw new FetcherException("Downloading failed :\n" + source + " -> " + destination + "\n" + ex.getMessage());
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
	public boolean extract(String source, String subsource, String destination, String hash, long size)
			throws FetcherException, FileListItemException {

		FileInputStream fin;
		BufferedInputStream bin;

		try (OutputStream out = new FileOutputStream(destination)) {
			fin = new FileInputStream(source);
			bin = new BufferedInputStream(fin);
			try (ZipInputStream zin = new ZipInputStream(bin)){
				ZipEntry ze;
				long extracted = 0;
				while ((ze = zin.getNextEntry()) != null)
					if (ze.getName().equals(subsource)) {
						byte[] buffer = new byte[BLOCK_SIZE];
						int len;
						while ((len = zin.read(buffer)) != -1) {
							out.write(buffer, 0, len);
							if (size > 0) {
								extracted += len;
								if (gui != null)
									gui.subProgress((double)(extracted) / size, "Extracting");
							}
						}
						break;
					}
			}

		} catch (Exception ex) {
			throw new FetcherException("Unpacking failed :\n" + source + " -> " + destination + "\n" + ex.getMessage());
		}

		return (!hash.isEmpty() ? FileListItem.checkFile(destination, hash, size) : true);
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
	) throws FetcherException, FileListItemException {
		if (zipFiles.contains(zipDestination)) {
			if (totalSize > 0)
				downloaded += size;
		} else {
			if(!download(source, zipDestination, zipHash, zipSize, size))
				return false;

			zipFiles.add(zipDestination);
		}
		try {
			if (extract(zipDestination, zipFilePath, destination, hash, size))
				return true;

		} catch (FetcherException ex) {
			if (totalSize > 0)
				downloaded -= size; //Extracting failed

			throw ex;
		} catch (FileListItemException ex) { //Forgive me for this code duplication
			if (totalSize > 0)
				downloaded -= size; //Extracting failed

			throw ex;
		}

		return false;
	}
	public void clear() {
		String filePath;
		for (Iterator<String> iter = zipFiles.iterator(); iter.hasNext(); ) {
			filePath = iter.next();
			(new File(filePath)).delete();
		}
		zipFiles.clear();
	}
}
