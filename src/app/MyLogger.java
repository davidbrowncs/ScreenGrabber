
package app;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;

import fileHandling.PathGetter;

public class MyLogger
{
	private Logger log;
	private String className;
	private static boolean formatSet = false;

	public static final String logFile = "/log.txt";

	public MyLogger(String className)
	{
		this.className = className;
		log = getLogger(className);
	}

	public MyLogger(Class<?> c)
	{
		log = getLogger(c);
	}

	private static Logger getLogger(String className)
	{
		Logger logger = Logger.getLogger(className);
		logger.setLevel(Level.ALL);
		return formatLogger(logger);
	}

	public Handler[] getHandlers()
	{
		return log.getHandlers();
	}

	private static Logger getLogger(Class<?> c)
	{
		return getLogger(c.getName());
	}

	public void severe(String message)
	{
		log.logp(Level.SEVERE, className, getMethodName(), message);
	}

	public void severe(String message, Object obj)
	{
		log.logp(Level.SEVERE, className, getMethodName(), message, obj);
	}

	public void warning(String message)
	{
		log.logp(Level.WARNING, className, getMethodName(), message);
	}

	public void warning(String message, Object obj)
	{
		log.logp(Level.WARNING, className, getMethodName(), message, obj);
	}

	public void info(String message)
	{
		log.logp(Level.INFO, className, getMethodName(), message);
	}

	public void info(String message, Object obj)
	{
		log.logp(Level.INFO, message, className, getMethodName(), obj);
	}

	public void debug(String message)
	{
		log.logp(Level.FINE, className, getMethodName(), message);
	}

	public void debug(String message, Object obj)
	{
		log.logp(Level.FINE, className, getMethodName(), message, obj);
	}

	private String getMethodName()
	{
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	private static Logger formatLogger(Logger logger)
	{
		if (!formatSet)
		{
			try
			{
				Logger log = LogManager.getLogManager().getLogger("");
				for (Handler h : log.getHandlers())
				{
					log.removeHandler(h);
				}

				Logger hookLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
				hookLogger.setLevel(Level.OFF);

				FileHandler handler = new FileHandler(PathGetter.getSavePath() + logFile, false);
				handler.setFormatter(new SingleLineFormatter());
				handler.setLevel(Level.ALL);
				log.addHandler(handler);

				ConsoleHandler h = new ConsoleHandler();
				h.setLevel(Level.ALL);
				h.setFormatter(new SingleLineFormatter());
				log.addHandler(h);
				formatSet = true;
				return logger;
			} catch (SecurityException | IOException e)
			{
				logger.logp(Level.WARNING, "MyLogger", "formatLogger", "Exception thrown when modifying logger", e);
			}
			return logger;
		} else
		{
			return logger;
		}
	}

	public static void close()
	{
		for (Handler h : Logger.getLogger("").getHandlers())
		{
			h.close();
		}
	}
}
