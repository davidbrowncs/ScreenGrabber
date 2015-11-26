
package app;

import java.text.NumberFormat;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;
import fileHandling.Configuration;

public class OptionsPane extends JPanel
{
	private Configuration config;

	/**
	 * Create the panel.
	 */
	public OptionsPane(Configuration c)
	{
		this.config = c;
		init();
	}

	private void init()
	{
		setLayout(new MigLayout("", "[194.00,grow]", "[][][][]"));

		JCheckBox chckbxPeriodicBackup = new JCheckBox("Backup Images?");
		if (config.isImmediateBackup() || config.isPeriodicBackup())
		{
			chckbxPeriodicBackup.setSelected(true);
		}
		add(chckbxPeriodicBackup, "cell 0 0,alignx center");

		JRadioButton rdbtnPeriodicBackup = new JRadioButton("Periodic Backup");
		if (config.isPeriodicBackup())
		{
			rdbtnPeriodicBackup.setSelected(true);
		}
		add(rdbtnPeriodicBackup, "cell 0 1,alignx center");

		JRadioButton rdbtnImmediateBackup = new JRadioButton("Immediate backup");
		if (config.isImmediateBackup())
		{
			rdbtnImmediateBackup.setSelected(true);
		}
		add(rdbtnImmediateBackup, "cell 0 2,alignx center");

		NumberFormat format = NumberFormat.getNumberInstance();
		JFormattedTextField frmtdtxtfldBackupPeriod = new JFormattedTextField(format);
		frmtdtxtfldBackupPeriod.addActionListener((e) ->
		{
			int delay = Integer.parseInt(frmtdtxtfldBackupPeriod.getText());
			if (delay > 0)
			{
				config.setBackupDelay(delay * 1000);
			}
		});
		frmtdtxtfldBackupPeriod.setColumns(2);
		frmtdtxtfldBackupPeriod.setText("Backup period");
		add(frmtdtxtfldBackupPeriod, "cell 0 3,growx");
	}

	public Configuration getConfig()
	{
		return config;
	}

}
