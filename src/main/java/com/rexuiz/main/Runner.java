package com.rexuiz.main;

import com.rexuiz.file.FileList;
import com.rexuiz.file.FileListItem;
import com.rexuiz.utils.LauncherUtils;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Runner extends Fetcher {

    private static final Logger log = Logger.getLogger(Runner.class.getName());
	private String rexuizHomeDir;
	private String rexuizLauncherCfg;
	private boolean notInstalled;
	private boolean wasInstalled;
	private boolean isWin, isMac, isLinux, is64;
	private Properties localProperties;
	String[] syncURLs;

	public Runner() {
		String osName = System.getProperty("os.name").toLowerCase();
		String osArch = System.getProperty("os.arch").toLowerCase();
		notInstalled = false;
		wasInstalled = false;
		isWin = false;
		isMac = false;
		rexuizLauncherCfg = "";
		localProperties = new Properties();

		if (osArch.contains("64")) {
			is64 = true;
		} else {
			is64 = false;
		}

		if (osName.contains("win")) {
			isWin = true;
		} else if (osName.contains("mac")) {
			isMac = true;
		} else {
			isLinux = true;
		}

		rexuizHomeDir = buildAppHome();
		Properties properties = new Properties();

		try (InputStream input =
					 Thread.currentThread().getContextClassLoader().getResourceAsStream("launcher.properties")) {

			if (input != null) {
				properties.load(input);
			}
		} catch (IOException e) {
			//ignore
		}

		if (properties.getProperty("launcher.mode", "").equals("updater")) {
			try {
				rexuizHomeDir = (new File(Runner.class.getProtectionDomain()
						.getCodeSource()
						.getLocation()
						.toURI()
						.getPath())).getParentFile().getPath();
			} catch (Exception ex) {
				log.log(Level.ALL, ex.getLocalizedMessage());
			}
		} else {
			rexuizLauncherCfg = System.getProperty("user.home") + File.separator + ((isLinux || isMac) ? "." : "") + AppConstants.cfgName;
			try (InputStream input = new FileInputStream(new File(rexuizLauncherCfg))) {
				localProperties.load(input);
				rexuizHomeDir = localProperties.getProperty("launcher.datadir", rexuizHomeDir);
			} catch (Exception ex) {
				//ignore
			}
		}

		int i, n;
		for (i = 0; i < 100; i++) {
			if (properties.getProperty("launcher.url" + Integer.toString(i), "").isEmpty()) {
				break;
			}
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

	private String buildAppHome() {
		return LauncherUtils.getUserHomeDir() + File.separator + AppConstants.homeDir;
	}

	private void update(String syncURL, FileList newFileList) throws RunnerException, FetcherException, FileListItem.FileListItemException {
		Iterator<Map.Entry<String, FileListItem>> iterator;
		Map.Entry<String, FileListItem> mentry;
		String filePath;
		iterator = newFileList.entrySet().iterator();
		long totalSize = 0;
		FileListItem itemNew;
		this.setConnectTimeout(10000);
		this.setReadTimeout(10000);
		while (iterator.hasNext()) {
			mentry = iterator.next();
			itemNew = mentry.getValue();
			totalSize += itemNew.size;
		}
		iterator = newFileList.entrySet().iterator();
		this.setDownloadSize(totalSize);
		this.status("Installing");
		FileListItem item;
		while (iterator.hasNext()) {
			mentry = iterator.next();
			filePath = (rexuizHomeDir + File.separator + mentry.getKey()).replace("/", File.separator);
			item = mentry.getValue();
			boolean needDownload = true;;
			if (!item.zipSource.equals("")) {
				try {
					needDownload = !downloadAndExtract(item.zipSource,
					                   filePath,
					                   item.hash,
					                   item.size,
					                   rexuizHomeDir + File.separator + item.zipSourceName,
					                   item.zipFilePath,
					                   item.zipHash,
					                   item.zipSize);
				} catch (Exception ex) {
					log.log(Level.ALL, ex.getLocalizedMessage());
					log.info("ignore and try another download usual way");
				}
			}
			if (needDownload && !download(syncURL + mentry.getKey(),
					filePath, item.hash, item.size)) {
				throw new RunnerException(filePath + ": file check failed");
			}
		}
	}
	private void prepareToRun() throws RunnerException, FetcherException, FileList.FileListException, FileListItem.FileListItemException {
		notInstalled = false;
		if (!(new File(rexuizHomeDir + File.separator + "index.lst")).exists()) {
			notInstalled = true;
			String tmp = rexuizHomeDir;
			rexuizHomeDir = askDestinationFolder(rexuizHomeDir);
			if (!rexuizHomeDir.equals(tmp) && !rexuizHomeDir.equals("")) {
				localProperties.setProperty("launcher.datadir", rexuizHomeDir);
				try (OutputStream out = new FileOutputStream(new File(rexuizLauncherCfg))) {
					localProperties.store(out, "Rexuiz Launcher config file");
				} catch (Exception ex) {
					//ignore
				}
			}
		}

		if (rexuizHomeDir.isEmpty()) {
			return;
		}

		this.status("Check for updates");
		String oldList = rexuizHomeDir + File.separator + "index.lst";
		FileList oldFileList = new FileList(oldList);
		Iterator<Map.Entry<String, FileListItem>> iterator;

		if (oldFileList.isEmpty()) {
			notInstalled = true;
		} else {
			notInstalled = false;
		}

		String syncURL = "";
		String updateList = rexuizHomeDir + File.separator + "index.lst.update";
		this.setConnectTimeout(1000); //Avoid long waiting
		this.setReadTimeout(1000);
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
			if (!FileListItem.checkFile((rexuizHomeDir + File.separator
                    + mentry.getKey()).replace("/", File.separator), "", itemOld.size)) {
                continue;
            }

			itemNew = newFileList.get(mentry.getKey());
			if (itemNew != null && itemNew.hash.equals(itemOld.hash)) {
				newFileList.remove(mentry.getKey());
			}
		}
		iterator = newFileList.entrySet().iterator();
		float updateSize = 0;
		while (iterator.hasNext()) {
			mentry = iterator.next();
			itemNew = newFileList.get(mentry.getKey());
			updateSize += itemNew.size;
			System.out.println(Long.toString(itemNew.size));
		}
		String updateSizeForm = String.format("%.01f", updateSize / 1000000.0f) + "MB";

		if (!newFileList.isEmpty() && ask((notInstalled ? "Install Rexuiz now?"
                : "Update available. Do you want install it?") + " Download data size: " + updateSizeForm)) {
			notInstalled = true;
			update(syncURL, newFileList);
			clear();
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
		if (isWin) {
			if (is64) {
				rexuizExe += AppConstants.runExeWin64;
			} else {
				rexuizExe += AppConstants.runExeWin32;
			}
		} else if (isMac) {
			rexuizExe += AppConstants.runExeMac;
			(new File(rexuizExe)).setExecutable(true, false);
		} else {
			if (is64) {
				rexuizExe += AppConstants.runExeLinux64;
			} else {
				rexuizExe += AppConstants.runExeLinux32;
			}
			(new File(rexuizExe)).setExecutable(true, false);
		}


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
			prepareToRun();
			if (wasInstalled) {
				if (this.ask("Rexuiz installed. Do you want run it?")) {
                    runRexuiz();
                }
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
			ex.printStackTrace(System.out);
		}
		System.exit(0);
	}
}
