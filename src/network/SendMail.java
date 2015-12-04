
package network;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {

	private final String username = "davidbrowncsdebug@gmail.com";
	private final String password = "";

	private File debugFile;

	public SendMail(File file) {
		this.debugFile = file;
	}

	public void go() {
		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username));
			message.setSubject("Application dump from: " + IPChecker.getIp());

			BodyPart bodyPart = new MimeBodyPart();
			bodyPart.setText("Log dump");

			Multipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(bodyPart);

			bodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(debugFile.getAbsolutePath());
			bodyPart.setDataHandler(new DataHandler(source));
			bodyPart.setFileName("Log dump");

			multiPart.addBodyPart(bodyPart);
			message.setContent(multiPart);

			Transport.send(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}