package com.rexuiz.file;

import java.util.*;
import java.io.*;

public class FileList extends HashMap<String, FileListItem> {
	public FileList(String path) {
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