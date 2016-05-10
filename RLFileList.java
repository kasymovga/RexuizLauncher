import java.util.*;
import java.io.*;

public class RLFileList extends HashMap<String, RLFileListItem> {
	public RLFileList(String path) {
		BufferedReader br = null;
		FileInputStream in = null;
		long size;
		try {
			in = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine()) != null) {
				line.replace("/", File.separator);
				String[] separated = line.split("\\|");
				if (separated.length == 3) {
					size = Integer.parseInt(separated[1]);
					this.put(separated[2], new RLFileListItem(separated[0], size));
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
