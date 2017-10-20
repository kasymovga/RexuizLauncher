package com.rexuiz.file;

import java.util.*;
import java.io.*;

public class FileList extends HashMap<String, FileListItem> {
	public FileList(String path) throws FileListException {
		BufferedReader br = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				String[] separated = line.split("\\|");
				if (separated.length == 3) {
					//field 1: hash
					//field 2: size
					//field 3: file path
					this.put(separated[2], new FileListItem(separated[0], Integer.parseInt(separated[1])));
				} else if (separated.length == 8) {
					//field 1: hash
					//field 2: size
					//field 3: file path
					//field 4: zip source
					//field 5: zip name
					//field 6: zip file path
					//field 7: zip hash
					//field 8: zip size
					//This record have higher priority, so remove compat record first
					this.remove(separated[2]);
					this.put(separated[2], new FileListItem(
							separated[0], //hash
							Integer.parseInt(separated[1]), //size
							separated[3], //zip source
							separated[4], //zip name
							separated[5], //zip file path
							separated[6], //zip hash
							Integer.parseInt(separated[7]) //zip size
					));
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
