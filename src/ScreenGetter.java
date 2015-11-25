import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

public class ScreenGetter implements ClipboardOwner
{
	private ArrayList<JWindow> windows = new ArrayList<>();
	private boolean frameVisible;
	private Object frameVisibleLock = new Object();

	private MyRectangle rect = null;
	private BufferedImage img;
	private JPanel debugPanel;

	private boolean primed = false;

	private boolean debugMode;

	public ScreenGetter(boolean debug)
	{
		this.debugMode = debug;
		init();
		addListeners();
	}

	public ScreenGetter()
	{
		this(false);
	}

	public boolean primed()
	{
		return primed;
	}

	public void prime(boolean b)
	{
		this.primed = b;
	}

	public static void main(String[] args)
	{
		ScreenGetter g = new ScreenGetter(false);
	}

	private void addListeners()
	{
		GlobalKeyListener l = new GlobalKeyListener(this);
		try
		{
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e)
		{
			e.printStackTrace();
		}
		MouseListener m = new MouseListener(this);

		GlobalScreen.addNativeKeyListener(l);
		GlobalScreen.addNativeMouseListener(m);
		GlobalScreen.addNativeMouseMotionListener(m);

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);
	}

	public void createRect(int x, int y)
	{
		rect = new MyRectangle(x, y);
	}

	public void hideWindow()
	{
		SwingUtilities.invokeLater(() ->
		{
			for (JWindow window : windows)
			{
				window.setVisible(false);
			}
		});
	}

	public void setWindowVisible()
	{
		SwingUtilities.invokeLater(() ->
		{
			for (JWindow window : windows)
			{
				window.setVisible(true);
			}
		});
	}

	private boolean windowVisible()
	{
		synchronized (frameVisibleLock)
		{
			return frameVisible;
		}
	}

	public void releaseRect()
	{
		rect = null;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents)
	{}

	public void toClipboard()
	{
		while (windowVisible())
		{}
		captureImage();
		TransferableImage trans = new TransferableImage(img);
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(trans, this);
		if (debugMode)
		{
			debugPanel.repaint();
		}
	}

	private void captureImage()
	{
		Rectangle screenRect = rect.getRect();
		BufferedImage capture = null;
		try
		{
			capture = new Robot().createScreenCapture(screenRect);
		} catch (AWTException e)
		{
			e.printStackTrace();
		}
		img = capture;
	}

	public void updateRectangle(int x, int y)
	{
		rect.setUpdatingX(x);
		rect.setUpdatingY(y);
		if (windowVisible())
		{
			for (JWindow window : windows)
			{
				window.repaint();
			}
		}
	}

	@SuppressWarnings("serial")
	private void init()
	{
		if (debugMode)
		{
			JFrame debugFrame = new JFrame("Debug");
			debugPanel = new JPanel()
			{
				@Override
				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					if (img != null)
					{
						g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
					}
				}
			};
			debugFrame.setBounds(500, 0, 500, 500);
			debugFrame.add(debugPanel);
			debugFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			debugFrame.setVisible(true);
		}

		new SystemTrayRunner();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (GraphicsDevice device : gs)
		{
			GraphicsConfiguration[] gcs = device.getConfigurations();
			for (GraphicsConfiguration gc : gcs)
			{
				Rectangle bounds = gc.getBounds();

				JWindow window = new JWindow()
				{
					private double xOffset = bounds.getX();
					private double yOffset = bounds.getY();
					private double width = bounds.getWidth();
					private double height = bounds.getHeight();

					@Override
					public void paint(Graphics gr)
					{
						if (rect != null)
						{
							super.paint(gr);
							Graphics2D g = (Graphics2D) gr.create();
							Rectangle r = rect.getRect();
							int x = (int) (r.getX() - xOffset);
							int y = (int) (r.getY() - yOffset);
							int rW = (int) (r.getWidth());
							int rH = (int) (r.getHeight());

							Composite original = g.getComposite();
							AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.CLEAR);
							g.setComposite(ac);
							g.setColor(new Color(0, 0, 0, 0));
							g.fillRect(x, y, rW, rH);

							g.setComposite(original);
							g.setColor(Color.CYAN);
							g.drawRect(x, y, rW, rH);

							g.dispose();
						}
					}

					@Override
					public void update(Graphics g)
					{
						paint(g);
					}
				};
				windows.add(window);
				window.setAlwaysOnTop(true);
				window.setBounds(bounds);
				window.setBackground(new Color(100, 100, 100, 100));
				window.addComponentListener(new ComponentAdapter()
				{
					@Override
					public void componentHidden(ComponentEvent e)
					{
						synchronized (frameVisibleLock)
						{
							frameVisible = false;
						}

					}

					@Override
					public void componentShown(ComponentEvent e)
					{
						synchronized (frameVisibleLock)
						{
							frameVisible = true;
						}
					}
				});
			}
		}
	}

	private class TransferableImage implements Transferable
	{
		Image i;

		public TransferableImage(Image i)
		{
			this.i = i;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			DataFlavor[] flavors = new DataFlavor[1];
			flavors[0] = DataFlavor.imageFlavor;
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			DataFlavor[] flavors = getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++)
			{
				if (flavor.equals(flavors[i]))
				{
					return true;
				}
			}
			return false;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
		{
			if (flavor.equals(DataFlavor.imageFlavor) && i != null)
			{
				return i;
			} else
			{
				throw new UnsupportedFlavorException(flavor);
			}
		}

	}
}
