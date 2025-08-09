package mj.ecom.notificationservice.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.ecom.notificationservice.dto.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:noreply.jashwanthreddy@gmail.com}")
    private String sender;

    // Main method to send emails (supports both text and HTML)
    public void sendSimpleMail(EmailDetails details) {
        try {
            log.info("Sending mail to {} ", details.getRecipient());

            // Check if HTML content should be sent
            if (details.isHtml()) {
                sendHtmlMail(details);
            } else {
                sendPlainTextMail(details);
            }

            log.info("Mail Sent Successfully to {}", details.getRecipient());
        } catch (Exception e) {
            log.error("Error while sending email to {}: {}", details.getRecipient(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // Private method to send plain text email
    private void sendPlainTextMail(EmailDetails details) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(details.getRecipient());
        message.setText(details.getMsgBody());
        message.setSubject(details.getSubject());
        javaMailSender.send(message);
    }

    // Private method to send HTML email
    private void sendHtmlMail(EmailDetails details) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(sender);
        helper.setTo(details.getRecipient());
        helper.setSubject(details.getSubject());
        helper.setText(details.getMsgBody(), true); // true indicates HTML content

        javaMailSender.send(mimeMessage);
    }

    // Convenience method specifically for HTML emails
    /*public void sendHtmlEmail(String to, String subject, String htmlContent) {
        EmailDetails details = new EmailDetails();
        details.setRecipient(to);
        details.setSubject(subject);
        details.setMsgBody(htmlContent);
        details.setHtml(true);
        sendSimpleMail(details);
    }*/

    // Test method to verify HTML email functionality
    /*public void sendTestHtmlEmail(String recipient) {
        try {
            String testHtmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Test HTML Email</title>
                    </head>
                    <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                            <h1 style="color: #333; text-align: center;">🎉 HTML Email Test</h1>
                            <p style="color: #666; font-size: 16px;">
                                If you can see this styled content, your HTML email configuration is working perfectly!
                            </p>
                            <div style="background-color: #d4edda; border: 1px solid #c3e6cb; padding: 15px; border-radius: 5px; margin: 20px 0;">
                                <strong style="color: #155724;">✅ Success!</strong> 
                                <span style="color: #155724;">Your JavaMailSender bean is configured correctly for HTML emails.</span>
                            </div>
                            <p style="text-align: center; margin-top: 30px;">
                                <span style="background: linear-gradient(45deg, #667eea, #764ba2); color: white; padding: 10px 20px; border-radius: 20px; text-decoration: none; display: inline-block;">
                                    mj-ecom Notification Service
                                </span>
                            </p>
                        </div>
                    </body>
                    </html>
                    """;

            EmailDetails testEmail = new EmailDetails();
            testEmail.setRecipient(recipient);
            testEmail.setSubject("🧪 HTML Email Configuration Test - mj-ecom");
            testEmail.setMsgBody(testHtmlContent);
            testEmail.setHtml(true);

            sendSimpleMail(testEmail);
            log.info("Test HTML email sent successfully to {}", recipient);

        } catch (Exception e) {
            log.error("Failed to send test HTML email to {}: {}", recipient, e.getMessage(), e);
            throw new RuntimeException("Test HTML email failed", e);
        }
    }*/
}
