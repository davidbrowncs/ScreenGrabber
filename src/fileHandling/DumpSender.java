
package fileHandling;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import app.MyLogger;
import net.miginfocom.swing.MigLayout;

public class DumpSender extends JPanel
{
	private SendMail mailSender;

	public DumpSender(Exception e)
	{
		MyLogger.close();
		this.setPreferredSize(new Dimension(300, 600));

		// errorFrame.setResizable(false);

		setLayout(new MigLayout("", "[grow][grow]", "[][grow][][]"));
		JLabel lblAnUnexpectedError = new JLabel("An unexpected error occured:");
		add(lblAnUnexpectedError, "cell 0 0 2 1,alignx center");

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 2 1,grow");

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		textArea.setText(e.getMessage());

		JLabel lblWouldYouLike = new JLabel("Would you like to send a log of the error to the developer?");
		add(lblWouldYouLike, "cell 0 2 2 1,alignx center");

		JButton yesButton = new JButton("Yes");
		yesButton.addActionListener((e1) ->
		{
			sendMessageAndExit();
		});
		add(yesButton, "cell 0 3,alignx center");

		JButton noButton = new JButton("No");
		noButton.addActionListener((e2) ->
		{
			System.exit(0);
		});
		add(noButton, "cell 1 3,alignx center");

		mailSender = new SendMail(new File(PathGetter.getSavePath() + MyLogger.logFile));

		JFrame errorFrame = new JFrame("An error occurred");
		errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		errorFrame.add(this);
		errorFrame.pack();
		errorFrame.setLocationRelativeTo(null);
		errorFrame.setVisible(true);
	}

	private void sendMessageAndExit()
	{
		Thread t = new Thread(() ->
		{
			mailSender.go();
			System.exit(0);
		});
		t.start();
	}
}
