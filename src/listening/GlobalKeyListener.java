
package listening;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import app.Log;
import app.ScreenGetter;
import fileHandling.Configuration;

public class GlobalKeyListener implements NativeKeyListener
{
	private Log log = new Log(GlobalKeyListener.class);

	private ScreenGetter g;
	private boolean ctrlPressed = false;
	private Configuration config;
	private int lastKeyPressed;

	public GlobalKeyListener(ScreenGetter g, Configuration c)
	{
		this.g = g;
		this.config = c;
		lastKeyPressed = c.getOperatorKeyCode();
	}

	public synchronized int getLastKeyPressed()
	{
		return lastKeyPressed;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0)
	{
		lastKeyPressed = arg0.getKeyCode();
		log.debug("Keycode of last key pressed: " + lastKeyPressed);
		if (arg0.getKeyCode() == NativeKeyEvent.VC_ESCAPE)
		{
			g.prime(false);
			g.hideWindow();
		} else if (arg0.getKeyCode() == NativeKeyEvent.VC_CONTROL_L || arg0.getKeyCode() == NativeKeyEvent.VC_CONTROL_R)
		{
			ctrlPressed = true;
		} else if (arg0.getKeyCode() == config.getOperatorKeyCode())
		{
			if (ctrlPressed)
			{
				g.setWindowVisible();
				g.prime(true);
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0)
	{
		if (arg0.getKeyCode() == NativeKeyEvent.VC_CONTROL_L || arg0.getKeyCode() == NativeKeyEvent.VC_CONTROL_R)
		{
			ctrlPressed = false;
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0)
	{}

}
