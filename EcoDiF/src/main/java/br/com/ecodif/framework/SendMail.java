package br.com.ecodif.framework;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Classe responsável pelo envio de e-mails de notificação
 * @author Bruno Costa
 *
 */
public class SendMail {

	/**
	 * Nome do usuário de e-mail
	 */
	final String username = "ubicomp.ufrj@gmail.com";
	
	/**
	 * Senha do usuário
	 */
	final String password = "flatronips236";

	/**
	 * Nome do remetente da mensagem
	 */
	private String sender;
	
	/**
	 * Nome do destinatário da mensagem
	 */
	private String receiver;
	
	/**
	 * Assunto
	 */
	private String subject;
	
	/**
	 * Corpo da mensagem
	 */
	private String messageMail;

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessageMail() {
		return messageMail;
	}

	public void setMessageMail(String messageMail) {
		this.messageMail = messageMail;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void sendMailSSL() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(receiver));
			message.setSubject(subject);
			message.setText(messageMail);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.print("Erro: " + e.getMessage() + exceptionAsString);
		}
	}
}
