package com.erikv121.email;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;
    private final String fromAddress;
    private final String toAddress;

    public EmailService(
            @Value("${resend.api-key}") String resendApiKey,
            @Value("${app.email.from}") String fromAddress,
            @Value("${app.email.to}") String toAddress) {
        this.resend = new Resend(resendApiKey.trim());
        this.fromAddress = fromAddress.trim();
        this.toAddress = toAddress.trim();
    }

    public void sendSimpleMail(EmailDto emailDto) {
        String sender = cleanHeader(require(emailDto.sender(), "sender"));
        String subject = cleanHeader(require(emailDto.subject(), "subject"));
        String body = require(emailDto.body(), "body").trim();

        validateLengths(sender, subject, body);

        String textBody = """
                New contact form message
                
                From: %s
                Subject: %s
                
                Message:
                %s
                """.formatted(sender, subject, body);

        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromAddress)
                    .to(toAddress)
                    .replyTo(sender)
                    .subject("[Portfolio] " + subject)
                    .text(textBody)
                    .build();

            CreateEmailResponse resp = resend.emails().send(options);
            log.info("Contact email sent via Resend. id={}", resp.getId());
        } catch (Exception e) {
            log.warn("Error while sending mail via Resend: {}", e.getMessage(), e);
            throw new EmailErrorException("Error sending mail");
        }
    }

    private static String require(String s, String fieldName) {
        if (s == null || s.isBlank()) {
            throw new EmailErrorException("Missing field: " + fieldName);
        }
        return s;
    }

    private static String cleanHeader(String s) {
        return s.replace("\r", " ").replace("\n", " ").trim();
    }

    private static void validateLengths(String sender, String subject, String body) {
        if (sender.length() > 254) throw new EmailErrorException("Sender email too long");
        if (subject.length() > 140) throw new EmailErrorException("Subject too long");
        if (body.length() > 4000) throw new EmailErrorException("Message too long");
    }
}
