import java.io.*;
import java.util.*;

public class RLRunner extends RLFetcher {
	String rexuizHomeDir;
	boolean notInstalled;
	boolean wasInstalled;
	public RLRunner() {
		notInstalled = false;
		wasInstalled = false;
		rexuizHomeDir = System.getProperty("user.home") + File.separator + RLConstants.homeDir;
	}
	public void checkUpdate() {
		this.status("Check for updates");
		String oldList = rexuizHomeDir + File.separator + "index.lst";
		RLFileList oldFileList = new RLFileList(oldList);
		Iterator<Map.Entry<String, RLFileListItem>> iterator;
		iterator = oldFileList.entrySet().iterator();
		if (!iterator.hasNext())
			notInstalled = true;
		String syncURL = "";
		String updateList = rexuizHomeDir + File.separator + "index.lst.update";
		int i;
		for (i = 0; i < RLConstants.syncURLs.length; i++) {
			if (this.download(RLConstants.syncURLs[i] + File.separator + "index.lst", updateList, 0))
			{
				syncURL = RLConstants.syncURLs[i];
				break;
			}
		}
		if (i == RLConstants.syncURLs.length && notInstalled)
			return;

		RLFileListItem itemOld, itemNew;
		Map.Entry<String, RLFileListItem> mentry;
		RLFileList newFileList = new RLFileList(updateList);
		iterator = oldFileList.entrySet().iterator();
		while (iterator.hasNext()) {
			mentry = iterator.next();
			itemOld = mentry.getValue();
			itemNew = newFileList.get(mentry.getKey());
			if (itemNew != null && itemNew.hash.equals(itemOld.hash) && itemNew.size == itemNew.size) {
				newFileList.remove(mentry.getKey());
			}
		}

		iterator = newFileList.entrySet().iterator();
		if (iterator.hasNext() && this.ask(notInstalled ? "Install Rexuiz now?" : "Update available. Do you want install it?")) {
			long totalSize = 0;
			while (iterator.hasNext()) {
				mentry = iterator.next();
				itemNew = mentry.getValue();
				totalSize += itemNew.size;
			}
			iterator = newFileList.entrySet().iterator();
			this.setDownloadSize(totalSize);
			this.status("Downloading game data");
			while (iterator.hasNext()) {
				mentry = iterator.next();
				itemNew = mentry.getValue();
				if (!this.download(syncURL + File.separator + mentry.getKey(), rexuizHomeDir + File.separator + mentry.getKey(), itemNew.size)) {
					notInstalled = true;
					return;
				}
			}
			(new File(oldList)).delete();
			(new File(updateList)).renameTo(new File(oldList));
			if (notInstalled) {
				wasInstalled = true;
				notInstalled = false;
			}
		}
	}
	void runRexuiz() {
		this.status("Running");
		String rexuizExe = rexuizHomeDir + File.separator;
		String osName = System.getProperty("os.name").toLowerCase();
		String osArch = System.getProperty("os.arch").toLowerCase();
		boolean is64 = false;
		if (osArch.contains("64")) {
			is64 = true;
			System.out.println("x86_64 detected");
		}
		if (osName.contains("win")) {
			if (is64) {
				rexuizExe += RLConstants.runExeWin64;
			} else {
				rexuizExe += RLConstants.runExeWin32;
			}
			System.out.println("windows detected");
		} else if (osName.contains("linux")) {
			if (is64) {
				rexuizExe += RLConstants.runExeLinux64;
			} else {
				rexuizExe += RLConstants.runExeLinux32;
			}
			System.out.println("linux detected");
			(new File(rexuizExe)).setExecutable(true, false);
		}


		String[] cmd = { rexuizExe };
		System.out.println("rexuizExe=" + rexuizExe);
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception ex) {
		}
	}
	public void whenStarted() {
		System.out.println("RLRunner: whenStarted()");
		checkUpdate();
		if (wasInstalled) {
			if (this.ask("Rexuiz installed. Do you want run it?"))
				runRexuiz();
		} else if (!notInstalled) {
			runRexuiz();
		}
		System.exit(0);
	}
}
