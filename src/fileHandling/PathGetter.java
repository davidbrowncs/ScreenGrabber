
package fileHandling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileSystemView;

public abstract class PathGetter {

	private static Logger log = Logger.getAnonymousLogger();

	private static String folder = ".screenGetter";
	private static String logFile = "/log.txt";
	private static String OS;

	public static String getDefaultDirectory() {
		return FileSystemView.getFileSystemView().getDefaultDirectory().toString();
	}

	public static String getLoggingPath() {
		return getSettingsPath() + logFile;
	}

	// DO NOT LOG IN HERE
	public static String getSettingsPath() {
		String path = System.getProperty("user.home");
		ensureFolderExists(path);
		String sep = getPathSeparator();
		return path + sep + folder;
	}

	private static String getPathSeparator() {
		return "/";
	}

	private static String getOSName() {
		if (OS == null) {
			OS = System.getProperty("os.name");
		}
		return OS;
	}

	private static boolean isWindows() {
		return getOSName().startsWith("Windows");
	}

	private static boolean isLinux() {
		return getOSName().startsWith("Linux");
	}

	private static boolean isMac() {
		return getOSName().startsWith("Mac");
	}

	private static void ensureFolderExists(String path) {
		File directory = new File(path);
		File[] files = directory.listFiles();

		log.logp(Level.INFO, PathGetter.class.getName(), "ensureFolderExists", "Searching files: " + Arrays.toString(files));

		String pathSeparator = getPathSeparator();

		boolean folderFound = false;

		if (files != null) {
			for (File f : files) {
				if (!f.isDirectory()) {
					continue;
				}
				String fPth = f.getAbsolutePath();
				Path p = Paths.get(fPth);
				String fileName;
				Path pa = p.getFileName();
				if (pa != null) {
					fileName = pa.toString();
				} else {
					continue;
				}

				if (fileName.startsWith(".screenGetter")) {
					folder = fileName;
					folderFound = true;

					log.logp(Level.INFO, PathGetter.class.getName(), "ensureFolderExists", "Found configuration folder "
							+ fPth);

					break;
				}
			}
		}

		if (!folderFound) {
			log.logp(Level.INFO, PathGetter.class.getName(), "ensureFolderExists",
					"Could not find configuration folder, creating now");
			File finalFolder = new File(path + pathSeparator + folder);
			int counter = 0;
			while (true) {
				if (finalFolder.exists()) {
					finalFolder = new File(path + pathSeparator + folder + counter);
					counter++;
				} else {
					finalFolder.mkdir();

					log.logp(Level.INFO, PathGetter.class.getName(), "ensureFolderExists", "Folder being created: ",
							finalFolder);

					if (isWindows()) {
						Path tmpPath = Paths.get(finalFolder.toURI());
						try {
							Files.setAttribute(tmpPath, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
							log.logp(Level.INFO, PathGetter.class.getName(), "ensureFolderExists",
									"Operating system is windows and the folder " + finalFolder.toString()
											+ " was set to hidden");
						} catch (IOException e) {
							log.logp(Level.WARNING, PathGetter.class.getName(), "ensureFolderExists",
									"Could not set the folder to hidden on dos ", e);
						}
					}
					break;
				}
			}
		} else {
			return;
		}
	}
}
