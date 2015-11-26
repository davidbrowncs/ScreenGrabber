
package fileHandling;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public final class FileHandler
{
	public static Configuration readConfiguration()
	{
		try
		{
			File file = new File(Configuration.getConfigPath());
			JAXBContext c = JAXBContext.newInstance(Configuration.class);

			System.out.println(file.exists());
			if (file.exists() && !file.isDirectory())
			{
				Unmarshaller u = c.createUnmarshaller();
				Configuration config = (Configuration) u.unmarshal(file);
				System.out.println(config);
				ensureHistoryFolder(config);
				return config;
			} else if (!file.exists())
			{
				Marshaller m = c.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				Configuration config = Configuration.getDefaultConfiguration();

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
			historyFolder.mkdir();
		} else if (historyFolder.exists() && !historyFolder.isDirectory())
		{
			System.err.println("History folder path exists and is not a folder");
		}
	}

	public static void writeConfiguration(Configuration c)
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(Configuration.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			m.marshal(c, new File(Configuration.getConfigPath()));

		} catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
}
