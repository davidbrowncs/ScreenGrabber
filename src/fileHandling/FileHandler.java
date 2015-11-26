
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

			if (file.exists() && !file.isDirectory())
			{
				Unmarshaller u = c.createUnmarshaller();
				Configuration config = (Configuration) u.unmarshal(file);
				File historyFolder = new File(config.getBackupPath());
				if (!historyFolder.exists())
				{
					historyFolder.mkdir();
				} else if (historyFolder.exists() && !historyFolder.isDirectory())
				{
					System.err.println("History folder path exists and is not a folder");
				}
				return config;
			} else if (!file.exists())
			{
				Marshaller m = c.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				Configuration config = Configuration.getDefaultConfiguration();
				m.marshal(config, file);

				File historyFolder = new File(config.getBackupPath());
				if (!historyFolder.exists())
				{
					historyFolder.mkdir();
				} else if (historyFolder.exists() && !historyFolder.isDirectory())
				{
					System.err.println("History folder path exists and is not a folder");
				}

				return config;
			}

		} catch (JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
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
