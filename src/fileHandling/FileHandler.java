
package fileHandling;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import app.MyLogger;

public final class FileHandler
{
	private static final MyLogger log = new MyLogger(FileHandler.class);

	public static Configuration readConfiguration()
	{
		try
		{
			File file = new File(Configuration.getConfigPath());
			JAXBContext c = JAXBContext.newInstance(Configuration.class);

			if (file.exists() && !file.isDirectory())
			{
				log.info("Found configuration file " + file.toString());

				Unmarshaller u = c.createUnmarshaller();
				Configuration config = (Configuration) u.unmarshal(file);

				log.info("Configuration found " + config.toString());

				ensureHistoryFolder(config);
				return config;
			} else if (!file.exists())
			{
				log.info("Could not find configuration file");

				Marshaller m = c.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				Configuration config = Configuration.getDefaultConfiguration();

				log.info("Created default configuration " + config.toString());

				m.marshal(config, file);
				ensureHistoryFolder(config);
				return config;
			}

		} catch (JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void ensureHistoryFolder(Configuration c)
	{
		File historyFolder = new File(c.getBackupPath());
		if (!historyFolder.exists())
		{
			log.info("Image history folder does not exist, creating it now " + historyFolder.toString());
			historyFolder.mkdir();
		} else if (historyFolder.exists() && !historyFolder.isDirectory())
		{
			log.warning("History folder exists already and is not a folder");
		}
	}

	public static void writeConfiguration(Configuration c)
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(Configuration.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			log.info("Writing configuration to file system " + c.toString());
			m.marshal(c, new File(Configuration.getConfigPath()));

		} catch (JAXBException e)
		{
			log.warning("Error when marshalling configuration " + e.getMessage());
		}
	}
}
