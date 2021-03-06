
package app;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import listening.GlobalKeyListener;
import fileHandling.Configuration;
import fileHandling.FileHandler;

public class SystemTrayRunner {
	private static Log log = new Log(SystemTrayRunner.class);

	private PopupMenu menu;
	private Configuration config;
	private BufferedImage icon;

	public SystemTrayRunner(Configuration c, GlobalKeyListener l) {
		this.config = c;
		init(l);
	}

	private void init(GlobalKeyListener l) {
		icon = FileHandler.loadImageResource("icon.png");
		menu = new PopupMenu();
		MenuItem item = new MenuItem("Options");
		item.addActionListener(e -> {
			log.debug("Showing the options window");
			JFrame tmpFrame = new JFrame("Options");
			tmpFrame.setResizable(false);
			tmpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			tmpFrame.setIconImage(icon);
			OptionsPane pane = new OptionsPane(config, l);
			tmpFrame.add(pane);
			tmpFrame.pack();
			tmpFrame.setLocationRelativeTo(null);
			tmpFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					log.info("Closing window and saving configuration: " + config.toString());
					FileHandler.writeConfiguration(config);
				}
			});
			tmpFrame.setVisible(true);
		});
		menu.add(item);

		item = new MenuItem("Exit");
		menu.add(item);
		item.addActionListener(e -> {
			log.info("Closing application");
			System.exit(0);
		});

		try {
			TrayIcon icon = new TrayIcon(this.icon);
			icon.setPopupMenu(menu);
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e1) {
			log.severe("Was unable to create a TrayIcon", e1);
		}

	}
}
