package com.rexuiz.file;

import java.util.*;
import java.io.*;

public class FileList extends HashMap<String, FileListItem> {
	public class FileListException  extends Exception {
		FileListException(String message) { super(message); }
	}
	public FileList(String path) throws FileListException {
		BufferedReader br = null;
		FileInputStream in = null;
		long size;
		try {
			in = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				String[] separated = line.split("\\|");
				if (separated.length == 3) {
					size = Integer.parseInt(separated[1]);
					this.put(separated[2], new FileListItem(separated[0], size));
				}
			}
		} catch (FileNotFoundException ex) {
		} catch (Exception ex) {
			throw new FileListException(path + ":\n" + ex.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception ex) {
				}
			} else if (in != null) {
				try {
					in.close();
				} catch (Exception ex) {
				}
			}
		}
	}
}
