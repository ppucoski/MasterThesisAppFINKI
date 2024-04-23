package vp.magisterski.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import vp.magisterski.service.EmailService;


@Service
public class EmailServiceImpl implements EmailService {

    private final MailSender mailSender;

    @Autowired
    public EmailServiceImpl(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailException exception) {
            // Handle exception
            System.out.println("test");
        }
    }
}
