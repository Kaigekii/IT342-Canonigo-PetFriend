package edu.cit.canonigo.petfriend.shared.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String fromAddress;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.enabled:true}") boolean enabled,
            @Value("${app.mail.from}") String fromAddress
    ) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.fromAddress = fromAddress;
    }

    public void sendWelcomeEmail(String toEmail, String firstName) {
        if (!enabled) {
            return;
        }
        if (toEmail == null || toEmail.isBlank()) {
            return;
        }

        String safeName = (firstName == null || firstName.isBlank()) ? "there" : firstName.trim();
        String subject = "Welcome to PetFriend";
        String body = String.join(
                "\n",
                "Hi " + safeName + ",",
                "",
                "Welcome to PetFriend. We are happy you are here.",
                "Your home for trusted pet care is ready whenever you are.",
                "",
                "If you need anything, reply to this email and our team will help.",
                "",
                "Warmly,",
                "The PetFriend Team"
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(fromAddress);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (Exception ex) {
            logger.warn("Failed to send welcome email to {}", toEmail, ex);
        }
    }
}
