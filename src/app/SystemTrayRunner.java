
package app;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import listening.GlobalKeyListener;
import fileHandling.Configuration;
import fileHandling.FileHandler;

public class SystemTrayRunner
{
	private PopupMenu menu;
	private Configuration config;

	public SystemTrayRunner(Configuration c, GlobalKeyListener l)
	{
		this.config = c;
		init(l);
	}

	private void init(GlobalKeyListener l)
	{
		menu = new PopupMenu();
		MenuItem item = new MenuItem("Options");
		item.addActionListener((e) ->
		{
			JFrame tmpFrame = new JFrame("Options");
			tmpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			OptionsPane pane = new OptionsPane(config, l);
			tmpFrame.add(pane);
			tmpFrame.pack();
			tmpFrame.setLocationRelativeTo(null);
			tmpFrame.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					FileHandler.writeConfiguration(config);
				}
			});
			tmpFrame.setVisible(true);
		});
		menu.add(item);

		item = new MenuItem("Exit");
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
