import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SystemTrayRunner
{
	private TrayIcon trayIcon;
	private PopupMenu menu;

	public SystemTrayRunner()
	{
		init();
	}

	private void init()
	{
		menu = new PopupMenu();
		MenuItem item = new MenuItem("Exit");
		menu.add(item);
		item.addActionListener((e) ->
		{
			System.exit(0);
		});

		try
		{
			TrayIcon icon = new TrayIcon(ImageIO.read(ResourceLoader.load("images/icon.png")));
			icon.setPopupMenu(menu);
			SystemTray.getSystemTray().add(icon);
		} catch (IOException | AWTException e1)
		{
			e1.printStackTrace();
		}

	}
}
