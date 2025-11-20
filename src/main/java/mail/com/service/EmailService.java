package mail.com.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public boolean sendEmail(String to, String subject, String body,String attachmentPath) {
        try {
            if (attachmentPath == null || attachmentPath.trim().isEmpty()) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                message.setFrom("20bit073@gbu.ac.in");

                mailSender.send(message);
                return true;
            }
            // Attachment present -> use MimeMessage to support attachments
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            boolean hasAttachment = true; // we have an attachmentPath non-empty
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, hasAttachment, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // keep as plain text; change to true if HTML
            helper.setFrom("20bit073@gbu.ac.in");

            File file = new File(attachmentPath);
            if (file.exists() && file.canRead()) {
                FileSystemResource resource = new FileSystemResource(file);
                helper.addAttachment(resource.getFilename(), resource);
            } else {
                // If attachment missing/unreadable, log and fall back to sending without attachment
                System.err.println("Attachment not found or unreadable: " + attachmentPath + ". Sending without attachment.");
                // send as simple message fallback
                SimpleMailMessage fallback = new SimpleMailMessage();
                fallback.setTo(to);
                fallback.setSubject(subject);
                fallback.setText(body);
                fallback.setFrom("20bit073@gbu.ac.in");
                mailSender.send(fallback);
                return true;
            }

            // send the mime message with attachment
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            return false;
        }
    }
}
