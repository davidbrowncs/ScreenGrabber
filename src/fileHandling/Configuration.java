
package fileHandling;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration
{
	private static final String CONFIGURATION_FILE_NAME = "config.xml";
	private static final String CONFIG_PATH = PathGetter.getSavePath() + "/" + CONFIGURATION_FILE_NAME;

	// Default versions of variables user can change
	private static final long DEFAULT_DELAY = 30000;
	private static final boolean DEFAULT_PERIODIC_BACKUP = false;
	private static final boolean IMMEDIATE_BACKUP = true;
	private static final String DEFAULT_BACKUP_PATH = PathGetter.getDefaultDirectory() + "/ScreenGetterHistory";

	private long backupDelay;
	private boolean periodicBackup;
	private boolean immediateBackup;
	private String backupPath;

	public static Configuration getDefaultConfiguration()
	{
		Configuration c = new Configuration();
		c.setBackupDelay(DEFAULT_DELAY);
		c.setPeriodicBackup(DEFAULT_PERIODIC_BACKUP);
		c.setImmediateBackup(IMMEDIATE_BACKUP);
		c.setBackupPath(DEFAULT_BACKUP_PATH);
		return c;
	}

	public synchronized long getBackupDelay()
	{
		return backupDelay;
	}

	public synchronized void setBackupDelay(long backupDelay)
	{
		this.backupDelay = backupDelay;
	}

	public synchronized boolean isPeriodicBackup()
	{
		return periodicBackup;
	}

	public synchronized void setPeriodicBackup(boolean periodicBackup)
	{
		this.periodicBackup = periodicBackup;
	}

	public synchronized String getBackupPath()
	{
		return backupPath;
	}

	public synchronized void setBackupPath(String backupPath)
	{
		this.backupPath = backupPath;
	}

	public synchronized boolean isImmediateBackup()
	{
		return immediateBackup;
	}

	public synchronized void setImmediateBackup(boolean immediateBackup)
	{
		this.immediateBackup = immediateBackup;
	}

	public static String getConfigurationFilePath()
	{
		return CONFIGURATION_FILE_NAME;
	}

	public static String getConfigPath()
	{
		return CONFIG_PATH;
	}

	@Override
	public synchronized String toString()
	{
		return "[BackupDelay:" + backupDelay + ";PeriodicBackup:" + periodicBackup + ";ImmediateBackup:" + immediateBackup
				+ ";BackupPath:" + backupPath + "]";
	}
}
