package tz.co.nezatech.apps.survey.web.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:hbs.properties")
public class EmailServiceImpl implements EmailService {

	@Autowired
	public JavaMailSender emailSender;
	@Value("${context.path}")
	String contextPath;

	@Override
	public void sendMail(String from, String to, String subject, String msg) {
		try {

			MimeMessage message = emailSender.createMimeMessage();

			message.setSubject(subject);
			MimeMessageHelper helper;
			helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setText(msg, true);
			emailSender.send(message);
		} catch (MessagingException ex) {
			Logger.getLogger(EmailServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * HttpServletRequest req, starting with /...
	 */
	@Override
	public String url(String path, HttpServletRequest req) {
		// String baseUrl = req.getRequestURI().split("/")[0];
		String baseUrl = String.format("%s://%s:%d", req.getScheme(), req.getServerName(), req.getServerPort());
		StringBuilder sb = new StringBuilder(baseUrl);
		sb.append("/").append(contextPath).append(path);
		return sb.toString();
	}
}
