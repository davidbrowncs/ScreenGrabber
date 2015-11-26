package app;
import java.awt.Rectangle;

public class MyRectangle
{
	private int startX;
	private int startY;
	private int updatingX;
	private int updatingY;

	public MyRectangle(int x, int y)
	{
		this.startX = x;
		this.startY = y;
	}

	public Rectangle getRect()
	{
		int finalX = startX > updatingX ? updatingX : startX;
		int finalY = startY > updatingY ? updatingY : startY;

		int finalWidth = startX < updatingX ? (updatingX - startX) : (startX - updatingX);
		int finalHeight = startY < updatingY ? (updatingY - startY) : (startY - updatingY);

		return new Rectangle(finalX, finalY, finalWidth, finalHeight);
	}

	public int getStartX()
	{
		return startX;
	}

	public void setStartX(int startX)
	{
		this.startX = startX;
	}

	public int getStartY()
	{
		return startY;
	}

	public void setStartY(int startY)
	{
		this.startY = startY;
	}

	public int getUpdatingX()
	{
		return updatingX;
	}

	public void setUpdatingX(int updatingX)
	{
		this.updatingX = updatingX;
	}

	public int getUpdatingY()
	{
		return updatingY;
	}

	public void setUpdatingY(int updatingY)
	{
		this.updatingY = updatingY;
	}

}
