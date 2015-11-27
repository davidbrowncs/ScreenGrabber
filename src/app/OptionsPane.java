
package app;

import java.awt.Dimension;
import java.text.NumberFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;

import listening.GlobalKeyListener;
import net.miginfocom.swing.MigLayout;

import org.jnativehook.keyboard.NativeKeyEvent;

import fileHandling.Configuration;

public class OptionsPane extends JPanel
{
	private static MyLogger log = new MyLogger(OptionsPane.class);

	private Configuration config;
	private JCheckBox chckbxPeriodicBackup = null;
	private GlobalKeyListener listener;

	/**
	 * Create the panel.
	 */
	public OptionsPane(Configuration c, GlobalKeyListener listener)
	{
		this.config = c;
		this.listener = listener;
		init();
	}

	private void init()
	{
		this.setPreferredSize(new Dimension(250, 250));
		setLayout(new MigLayout("", "[194.00,grow]", "[grow][grow][grow][grow][][grow][grow]"));
		ButtonGroup group = new ButtonGroup();

		JRadioButton rdbtnPeriodicBackup = new JRadioButton("Periodic Backup");
		if (config.isPeriodicBackup())
		{
			log.debug("Setting periodic backup radio button selected");
			rdbtnPeriodicBackup.setSelected(true);
		}
		rdbtnPeriodicBackup.addActionListener((e) ->
		{
			if (chckbxPeriodicBackup.isSelected())
			{
				config.setPeriodicBackup(rdbtnPeriodicBackup.isSelected());
				config.setImmediateBackup(false);
			} else
			{
				group.clearSelection();
				config.setPeriodicBackup(false);
			}
			log.debug("Periodic backup: " + config.isPeriodicBackup() + " immediate backup: " + config.isImmediateBackup());
		});
		add(rdbtnPeriodicBackup, "cell 0 1,alignx center");

		JRadioButton rdbtnImmediateBackup = new JRadioButton("Immediate backup");
		if (config.isImmediateBackup())
		{
			rdbtnImmediateBackup.setSelected(true);
		}
		rdbtnImmediateBackup.addActionListener((e) ->
		{
			if (chckbxPeriodicBackup.isSelected())
			{
				config.setImmediateBackup(rdbtnImmediateBackup.isSelected());
				config.setPeriodicBackup(false);

			} else
			{
				group.clearSelection();
				config.setImmediateBackup(false);
			}
			log.debug("Periodic backup: " + config.isPeriodicBackup() + " immediate backup: " + config.isImmediateBackup());
		});
		add(rdbtnImmediateBackup, "cell 0 2,alignx center");

		chckbxPeriodicBackup = new JCheckBox("Backup Images?");
		if (config.isImmediateBackup() || config.isPeriodicBackup())
		{
			chckbxPeriodicBackup.setSelected(true);
		} else
		{
			chckbxPeriodicBackup.setSelected(false);
			rdbtnImmediateBackup.setSelected(false);
			rdbtnPeriodicBackup.setSelected(false);
		}
		chckbxPeriodicBackup.addActionListener((e) ->
		{
			if (chckbxPeriodicBackup.isSelected())
			{
				rdbtnImmediateBackup.setSelected(true);
				rdbtnPeriodicBackup.setSelected(false);
			} else
			{
				group.clearSelection();
			}
			config.setImmediateBackup(rdbtnPeriodicBackup.isSelected());
			config.setImmediateBackup(rdbtnImmediateBackup.isSelected());
		});
		add(chckbxPeriodicBackup, "cell 0 0,alignx center");

		NumberFormatter format = new NumberFormatter();
		format.setOverwriteMode(true);
		NumberFormat f = NumberFormat.getInstance();
		f.setGroupingUsed(false);
		f.setMaximumIntegerDigits(2);
		format.setFormat(f);
		format.setAllowsInvalid(false);
		JFormattedTextField frmtdtxtfldBackupPeriod = new JFormattedTextField(format);
		frmtdtxtfldBackupPeriod.addActionListener((e) ->
		{
			int delay = Integer.parseInt(frmtdtxtfldBackupPeriod.getText().trim());
			if (delay > 0 && delay <= 60)
			{
				log.info("Periodic backup delay set to: " + (delay * 1000));
				config.setBackupDelay(delay * 1000);
			}
		});

		JLabel lblPeriodicBackupDelay = new JLabel("Periodic backup delay (in seconds):");
		add(lblPeriodicBackupDelay, "cell 0 3,alignx center,aligny bottom");
		frmtdtxtfldBackupPeriod.setText("" + (config.getBackupDelay() / 1000));
		add(frmtdtxtfldBackupPeriod, "cell 0 4,growx");

		group.add(rdbtnPeriodicBackup);
		group.add(rdbtnImmediateBackup);

		JLabel lblCurrentKey = new JLabel("Current key: " + NativeKeyEvent.getKeyText(config.getOperatorKeyCode()));
		add(lblCurrentKey, "cell 0 5,alignx center");

		JButton btnSetActionKey = new JButton("Set Action Key");
		btnSetActionKey.addActionListener((e) ->
		{
			final int prev = listener.getLastKeyPressed();
			Thread t = new Thread(new Runnable()
			{
				volatile int newCode;

				@Override
				public void run()
				{
					newCode = prev;
					log.debug("Previous key code: " + prev);
					while (true)
					{
						newCode = listener.getLastKeyPressed();
						if (newCode != prev)
						{
							break;
						}
					}
					log.debug("New key code: " + newCode);
					config.setOperatorKey(newCode);
					final int theKeyCode = newCode;
					SwingUtilities.invokeLater(() ->
					{
						log.debug("Setting key text: " + NativeKeyEvent.getKeyText(theKeyCode));
						lblCurrentKey.setText("Current key: " + NativeKeyEvent.getKeyText(theKeyCode));
					});
				}
			});
			t.start();
		});
		add(btnSetActionKey, "cell 0 6,alignx center");
	}

	class JTextFieldLimit extends PlainDocument
	{
		private int limit;

		JTextFieldLimit(int limit)
		{
			super();
			this.limit = limit;
		}

		JTextFieldLimit(int limit, boolean upper)
		{
			super();
			this.limit = limit;
		}

		@Override
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
		{
			if (str == null)
			{
				return;
			}

			if ((getLength() + str.length()) <= limit)
			{
				super.insertString(offset, str, attr);
			}
		}
	}

	public Configuration getConfig()
	{
		return config;
	}

}
