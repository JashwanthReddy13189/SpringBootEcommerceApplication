package mj.ecom.notificationservice.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.ecom.notificationservice.dto.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    // Method
    // To send a simple email
    public void sendSimpleMail(EmailDetails details) {
        try {
            log.info("Sending mail to {} ", details.getRecipient());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(details.getRecipient());
            message.setText(details.getMsgBody());
            message.setSubject(details.getSubject());

            // sending the mail
            javaMailSender.send(message);
            log.info("Mail Sent Successfully");
        } catch (Exception e) {
            log.error("Error while sending email");
        }
    }
}
