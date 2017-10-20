package com.rexuiz.utils;

public class LauncherUtils {

	public static OperatingSystem defineOperationSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return OperatingSystem.WINDOWS;
		} else if (osName.contains("mac")) {
			return OperatingSystem.MAC;
		} else {
			return OperatingSystem.LINUX;
		}
	}

	public static ProcessorArch defineProcessorArch() {
		String osArch = System.getProperty("os.arch").toLowerCase();
		if (osArch.contains("64")) {
			return ProcessorArch.X64;
		} else {
			return ProcessorArch.X32;
		}
	}

	public static String getUserHomeDir() {
		return System.getProperty("user.home");
	}
}
