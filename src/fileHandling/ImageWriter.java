
package fileHandling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import app.MyLogger;

public final class ImageWriter
{
	private static final MyLogger log = new MyLogger(ImageWriter.class);

	public static void writeImage(Configuration c, BufferedImage img)
	{
		try
		{
			FileHandler.ensureHistoryFolder(c);
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String timeStamp = format.format(date);

			File file = new File(c.getBackupPath() + "/" + timeStamp + ".png");

			int counter = 1;
			while (file.exists())
			{
				file = new File(c.getBackupPath() + "/" + timeStamp + "(" + counter + ").png");
				counter++;
			}
			log.info("Saving image to file: " + file.toString());

			ImageIO.write(img, "png", file);
		} catch (IOException e)
		{
			log.severe("Error when writing file " + e.getMessage());
		}
	}

}
