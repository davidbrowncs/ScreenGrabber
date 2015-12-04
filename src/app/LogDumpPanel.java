
package app;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;

public class LogDumpPanel extends JPanel {
	private static final long serialVersionUID = -8776736881898295660L;

	public LogDumpPanel(String s) {
		Log.close();
		this.setPreferredSize(new Dimension(800, 600));

		setLayout(new MigLayout("", "[grow][grow]", "[][grow][][][]"));
		JLabel lblAnUnexpectedError = new JLabel("An unexpected error occured:");
		add(lblAnUnexpectedError, "cell 0 0 2 1,alignx center");

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 2 1,grow");

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		textArea.setText(s);

		JLabel sendLogDumpMessage = new JLabel("Please send a copy of this error to the developer at:");
		add(sendLogDumpMessage, "cell 0 2 2 1,alignx center");

		JFrame errorFrame = new JFrame("An error occurred");
		errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		errorFrame.getContentPane().add(this);

		JTextArea emailPane = new JTextArea();
		emailPane.setEditable(false);
		emailPane.setText("davidbrowncsdebug@gmail.com");
		add(emailPane, "cell 0 3 2 1,growx,aligny center");

		JButton btnNewButton = new JButton("Exit");
		btnNewButton.addActionListener(e1 -> {
			exit();
		});
		add(btnNewButton, "cell 0 4 2 1,alignx center");
		errorFrame.pack();
		errorFrame.setResizable(false);
		errorFrame.setLocationRelativeTo(null);
		errorFrame.setVisible(true);
	}

	private void exit() {
		System.exit(1);
	}
}
