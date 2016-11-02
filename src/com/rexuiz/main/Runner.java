package com.rexuiz.main;

import com.rexuiz.file.FileList;
import com.rexuiz.file.FileListItem;

import java.io.*;
import java.util.*;

public class Runner extends Fetcher {
	public class RunnerException extends Exception {
		public RunnerException(String message) { super(message); }
	}
	private String rexuizHomeDir;
	private boolean notInstalled;
	private boolean wasInstalled;
	String[] syncURLs;
	public Runner() {
		notInstalled = false;
		wasInstalled = false;
		rexuizHomeDir = System.getProperty("user.home") + File.separator + AppConstants.homeDir;
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = Thread.currentThread().getContextClassLoader().getResourceAsStream("launcher.properties");
			if (input != null) {
				properties.load(input);
			}
		} catch (IOException e) {
			//ignore
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (Exception ex) {
					//ignore
				}
		}
		if (properties.getProperty("launcher.mode", "").equals("updater")) {
			try {
				rexuizHomeDir = (new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath();
			} catch (Exception ex) {
				//???
			}
		}

		int i, n;
		for (i = 0; i < 100; i++) {
			if (properties.getProperty("launcher.url" + Integer.toString(i), "") == "")
				break;
		}
		if (i == 0) {
			syncURLs = AppConstants.syncURLs;
		} else {
			n = i;
			syncURLs = new String[n];
			for (i = 0; i < n; i++) {
				syncURLs[i] = properties.getProperty("launcher.url" + Integer.toString(i));
			}
		}

	}
	private void update(String syncURL, FileList newFileList) throws RunnerException, FetcherException, FileListItem.FileListItemException {
		Iterator<Map.Entry<String, FileListItem>> iterator;
		Map.Entry<String, FileListItem> mentry;
		String filePath;
		iterator = newFileList.entrySet().iterator();
		long totalSize = 0;
		FileListItem itemNew;
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
			filePath = (rexuizHomeDir + File.separator + mentry.getKey()).replace("/", File.separator);
			if (!download(syncURL + mentry.getKey(),
					filePath, mentry.getValue().hash, mentry.getValue().size)) {
				throw new RunnerException(filePath + ": file check failed");
			}
		}
	}
	private void checkUpdate() throws RunnerException, FetcherException, FileList.FileListException, FileListItem.FileListItemException {
		this.status("Check for updates");
		String oldList = rexuizHomeDir + File.separator + "index.lst";
		FileList oldFileList = new FileList(oldList);
		Iterator<Map.Entry<String, FileListItem>> iterator;
		if (oldFileList.isEmpty())
			notInstalled = true;
		String syncURL = "";
		String updateList = rexuizHomeDir + File.separator + "index.lst.update";
		int i;
		for (i = 0; i < syncURLs.length; i++) {
			try {
				download(syncURLs[i] + "index.lst", updateList, "", 0);
				syncURL = syncURLs[i];
				break;
			} catch (Exception ex) {
			}
		}
		if (i == syncURLs.length)
			return;

		FileListItem itemOld, itemNew;
		Map.Entry<String, FileListItem> mentry;
		FileList newFileList = new FileList(updateList);
		iterator = oldFileList.entrySet().iterator();
		while (iterator.hasNext()) {
			mentry = iterator.next();
			itemOld = mentry.getValue();

			//Force update broken files
			if (!FileListItem.checkFile((rexuizHomeDir + File.separator + mentry.getKey()).replace("/", File.separator), "", itemOld.size))
				continue;

			itemNew = newFileList.get(mentry.getKey());
			if (itemNew != null && itemNew.hash.equals(itemOld.hash)) {
				newFileList.remove(mentry.getKey());
			}
		}

		if (!newFileList.isEmpty() && ask(notInstalled ? "Install Rexuiz now?" : "Update available. Do you want install it?")) {
			notInstalled = true;
			update(syncURL, newFileList);
			(new File(oldList)).delete();
			(new File(updateList)).renameTo(new File(oldList));
			wasInstalled = true;
			notInstalled = false;
		}
	}
	private static void inheritIO(final InputStream src, final PrintStream dest) {
		new Thread(new Runnable() {
			public void run() {
				Scanner sc = new Scanner(src);
				while (sc.hasNextLine()) {
					dest.println(sc.nextLine());
				}
			}
		}).start();
	}
	private void runRexuiz() throws RunnerException {
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
		} else if (osName.contains("mac")) {
			rexuizExe += AppConstants.runExeMac;
			(new File(rexuizExe)).setExecutable(true, false);
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
			Process p = new ProcessBuilder(rexuizExe).directory(new File(rexuizHomeDir)).start();
			inheritIO(p.getInputStream(), System.out);
			inheritIO(p.getErrorStream(), System.err);
			p.waitFor();
		} catch (Exception ex) {
			throw new RunnerException("Execute failed:\n" + rexuizExe + "\n" + ex.getMessage());
		}
	}
	public void run() {
		this.showMainDialog();
		try {
			checkUpdate();
			if (wasInstalled) {
				if (this.ask("Rexuiz installed. Do you want run it?"))
					runRexuiz();
			} else if (!notInstalled) {
				runRexuiz();
			}
		} catch (FetcherException ex) {
			message(ex.getMessage());
		} catch (RunnerException ex) {
			message(ex.getMessage());
		} catch (FileList.FileListException ex) {
			message(ex.getMessage());
		} catch (FileListItem.FileListItemException ex) {
			message(ex.getMessage());
		} catch (Exception ex) {
			message("Error:\n" + ex.getMessage());
		}
		System.exit(0);
	}
}
