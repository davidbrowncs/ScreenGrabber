import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

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
		} else if (arg0.getKeyCode() == 29)
		{
			ctrlPressed = true;
		} else if (arg0.getKeyCode() == 125)
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
		if (arg0.getKeyCode() == 29)
		{
			ctrlPressed = false;
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0)
	{

	}

}
