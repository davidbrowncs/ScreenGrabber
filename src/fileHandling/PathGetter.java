package fileHandling;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.filechooser.FileSystemView;

public final class PathGetter
{
	private static String folder = ".screenGetter";
	private static String OS;

	public static String getDefaultDirectory()
	{
		return FileSystemView.getFileSystemView().getDefaultDirectory().toString();
	}

	public static String getSavePath()
	{
		String path = System.getProperty("user.home");
		ensureFolderExists(path);
		String sep = getPathSeparator();
		return path + sep + folder;
	}

	private static String getPathSeparator()
	{
		return "/";
	}

	private static String getOSName()
	{
		if (OS == null)
		{
			OS = System.getProperty("os.name");
		}
		return OS;
	}

	private static boolean isWindows()
	{
		return getOSName().startsWith("Windows");
	}

	private static boolean isLinux()
	{
		return getOSName().startsWith("Linux");
	}

	private static boolean isMac()
	{
		return getOSName().startsWith("Mac");
	}

	private static void ensureFolderExists(String path)
	{
		File directory = new File(path);
		File[] files = directory.listFiles();

		String pathSeparator = getPathSeparator();

		boolean folderFound = false;
		for (File f : files)
		{
			if (!f.isDirectory())
			{
				continue;
			}
			String fPth = f.getAbsolutePath();
			Path p = Paths.get(fPth);
			String fileName = p.getFileName().toString();

			if (fileName.startsWith(".screenGetter"))
			{
				folder = fileName;
				folderFound = true;
				break;
			}
		}

		if (!folderFound)
		{
			File finalFolder = new File(path + pathSeparator + folder);
			int counter = 0;
			while (true)
			{
				if (finalFolder.exists())
				{
					finalFolder = new File(path + pathSeparator + folder + counter);
					counter++;
				} else
				{
					finalFolder.mkdir();
					if (isWindows())
					{
						Path tmpPath = Paths.get(finalFolder.toURI());
						try
						{
							Files.setAttribute(tmpPath, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
						} catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					break;
				}
			}
		} else
		{
			return;
		}
	}
}
