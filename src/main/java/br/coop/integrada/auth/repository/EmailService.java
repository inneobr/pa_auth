package br.coop.integrada.auth.repository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
public class EmailService {

	@Autowired
    private JavaMailSender mailSender;

	
    public void sendSimpleEmail(String destinatario, String assunto, String texto) {    	
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("servidores@integrada.coop.br");
        message.setTo(destinatario);
        message.setSubject(assunto);
        message.setText(texto);
        mailSender.send(message);
    }
	
	
	public void sendHtmlEmail(String destinatario, String assunto, String texto) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("servidores@integrada.coop.br");
        helper.setTo(destinatario);
        helper.setSubject(assunto);
        helper.setText(texto, true);
        mailSender.send(message);
    }
}