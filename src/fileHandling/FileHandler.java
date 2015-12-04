
package fileHandling;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import app.Log;

public final class FileHandler {

	private static final Log log = new Log(FileHandler.class);

	// No instantiating for you user
	private FileHandler() {}

	public static Configuration readConfiguration() {
		try {
			File file = new File(Configuration.getConfigPath());
			JAXBContext c = JAXBContext.newInstance(Configuration.class);

			if (file.exists() && !file.isDirectory()) {
				log.info("Found configuration file " + file.toString());

				Unmarshaller u = c.createUnmarshaller();
				Configuration config = (Configuration) u.unmarshal(file);

				log.info("Configuration found " + config.toString());

				ensureHistoryFolder(config);
				return config;
			} else if (!file.exists()) {
				log.info("Could not find configuration file");

				Marshaller m = c.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				Configuration config = Configuration.getDefaultConfiguration();

				log.info("Created default configuration " + config.toString());

				m.marshal(config, file);
				ensureHistoryFolder(config);
				return config;
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void ensureHistoryFolder(Configuration c) {
		File historyFolder = new File(c.getBackupPath());
		if (!historyFolder.exists()) {
			log.info("Image history folder does not exist, creating it now " + historyFolder.toString());
			historyFolder.mkdir();
		} else if (historyFolder.exists() && !historyFolder.isDirectory()) {
			log.warning("History folder exists already and is not a folder");
		}
	}

	public static void writeConfiguration(Configuration c) {
		try {
			JAXBContext context = JAXBContext.newInstance(Configuration.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			log.info("Writing configuration to file system " + c.toString());
			m.marshal(c, new File(Configuration.getConfigPath()));

		} catch (JAXBException e) {
			log.warning("Error when marshalling configuration " + e.getMessage());
		}
	}

	private static File getFile() {
		String normal = PathGetter.getLoggingPath();
		File file = new File(normal);
		File parent = new File(file.getParent());
		File[] files = parent.listFiles();
		if (files == null || files.length == 0) {
			log.warning("The save directory does not exist, or there were no files in the directory");
		}

		File newest = files[0];
		for (File f : files) {
			if (f.getName().contains("log") && newest.lastModified() < f.lastModified()) {
				newest = f;
			}
		}
		return newest;
	}

	public static String readLog() {
		File file = getFile();
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			log.warning("Error occured while reading log file " + e.toString());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.info("Could not close buffered reader " + e.toString());
				}
			}
		}
		return sb.toString();
	}

	public static void writeImage(Configuration c, BufferedImage img) {
		try {
			FileHandler.ensureHistoryFolder(c);
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String timeStamp = format.format(date);

			File file = new File(c.getBackupPath() + "/" + timeStamp + ".png");

			int counter = 1;
			while (file.exists()) {
				file = new File(c.getBackupPath() + "/" + timeStamp + "(" + counter + ").png");
				counter++;
			}
			log.info("Saving image to file: " + file.toString());

			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			log.severe("Error when writing file " + e.getMessage());
		}
	}

	private static InputStream getResourceStream(String path) {
		InputStream input = FileHandler.class.getResourceAsStream(path);
		if (input == null) {
			input = FileHandler.class.getResourceAsStream("/" + path);
		}
		return input;
	}

	public static BufferedImage loadImageResource(String fileName) {
		String path = "/images/" + fileName;
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			log.warning("File " + file + " does not exist, or is a directory");
			return null;
		}
		try {
			return ImageIO.read(getResourceStream(path));
		} catch (IOException e) {
			log.warning("Could not load image " + path);
		}
		return null;
	}
}
