package listening;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import app.ScreenGetter;

public class GlobalKeyListener implements NativeKeyListener
{
	private ScreenGetter g;
	private boolean ctrlPressed = false;

	public GlobalKeyListener(ScreenGetter g)
	{
		this.g = g;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0)
	{
		if (arg0.getKeyCode() == NativeKeyEvent.VC_ESCAPE)
		{
			g.prime(false);
			g.hideWindow();
		} else if (arg0.getKeyCode() == NativeKeyEvent.VC_CONTROL_L || arg0.getKeyCode() == NativeKeyEvent.VC_CONTROL_R)
		{
			ctrlPressed = true;
		} else if (arg0.getKeyCode() == NativeKeyEvent.VC_YEN)
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
