package com.rexuiz.main;

import com.rexuiz.file.FileList;
import com.rexuiz.file.FileListItem;

import java.io.*;
import java.util.*;

public class Runner extends Fetcher {
	String rexuizHomeDir;
	boolean notInstalled;
	boolean wasInstalled;
	public Runner() {
		notInstalled = false;
		wasInstalled = false;
		rexuizHomeDir = System.getProperty("user.home") + File.separator + AppConstants.homeDir;
	}
	public void checkUpdate() {
		this.status("Check for updates");
		String oldList = rexuizHomeDir + File.separator + "index.lst";
		FileList oldFileList = new FileList(oldList);
		Iterator<Map.Entry<String, FileListItem>> iterator;
		iterator = oldFileList.entrySet().iterator();
		if (!iterator.hasNext())
			notInstalled = true;
		String syncURL = "";
		String updateList = rexuizHomeDir + File.separator + "index.lst.update";
		int i;
		for (i = 0; i < AppConstants.syncURLs.length; i++) {
			if (download(AppConstants.syncURLs[i] + "index.lst", updateList, "", 0))
			{
				syncURL = AppConstants.syncURLs[i];
				break;
			}
		}
		if (i == AppConstants.syncURLs.length && notInstalled)
			return;

		FileListItem itemOld, itemNew;
		Map.Entry<String, FileListItem> mentry;
		FileList newFileList = new FileList(updateList);
		iterator = oldFileList.entrySet().iterator();
		while (iterator.hasNext()) {
			mentry = iterator.next();
			itemOld = mentry.getValue();
			itemNew = newFileList.get(mentry.getKey());
			if (itemNew != null && itemNew.hash.equals(itemOld.hash)) {
				newFileList.remove(mentry.getKey());
			}
		}

		iterator = newFileList.entrySet().iterator();
		if (iterator.hasNext() && ask(notInstalled ? "Install Rexuiz now?" : "Update available. Do you want install it?")) {
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
				if (!download(syncURL + mentry.getKey(),
						(rexuizHomeDir + File.separator + mentry.getKey()).replace("/",
						File.separator), mentry.getValue().hash, mentry.getValue().size)) {
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
		}
		if (osName.contains("win")) {
			if (is64) {
				rexuizExe += AppConstants.runExeWin64;
			} else {
				rexuizExe += AppConstants.runExeWin32;
			}
		} else if (osName.contains("linux")) {
			if (is64) {
				rexuizExe += AppConstants.runExeLinux64;
			} else {
				rexuizExe += AppConstants.runExeLinux32;
			}
			(new File(rexuizExe)).setExecutable(true, false);
		}


		String[] cmd = { rexuizExe };
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception ex) {
		}
	}
	public void run() {
		this.showMainDialog();
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
