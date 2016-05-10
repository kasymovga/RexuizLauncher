import java.io.*;
import java.net.*;
import java.lang.Exception;

public class RLFetcher extends RLGUI {
	private long totalSize;
	private long downloaded;
	public RLFetcher() {
		this.setDownloadSize(0);
	}
	public void setDownloadSize(long newTotalSize) {
		downloaded = 0;
		totalSize = newTotalSize;
	}
	public boolean download(String source, String destination, long size) {
		boolean success = true;
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			System.out.println("Source: ".concat(source));
			System.out.println("Destination: ".concat(destination));
			(new File(destination)).getParentFile().mkdirs();
			in = new BufferedInputStream(new URL(source).openStream());
			fout = new FileOutputStream(destination);

			final byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) > 0) {
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
				};
			}
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception ex) {
				};
			}
		}
		return success;
	}
}
