package mail.com.service;

import mail.com.entity.EmailCampaign;
import mail.com.entity.EmailStatus;
import mail.com.repository.EmailRecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BulkEmailService {
    @Autowired
    private EmailRecipientRepository recipientRepository;

    @Autowired
    private EmailService emailService;

    @Async
    public void sendBulkEmails(EmailCampaign campaign) {
        String attachmentPath = campaign.getAttachmentPath();
        campaign.getRecipients().forEach(recipient -> {
            try {
                // Apply delay between emails
                if (campaign.getDelaySeconds() != null && campaign.getDelaySeconds() > 0) {
                    Thread.sleep(campaign.getDelaySeconds() * 1000L);
                }

                boolean sent = emailService.sendEmail(
                        recipient.getEmail(),
                        campaign.getSubject(),
                        campaign.getBody(),
                        attachmentPath
                );

                if (sent) {
                    recipient.setStatus(EmailStatus.SENT);
                    recipient.setSentAt(LocalDateTime.now());
                } else {
                    recipient.setStatus(EmailStatus.FAILED);
                    recipient.setErrorMessage("Failed to send email");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                recipient.setStatus(EmailStatus.FAILED);
                recipient.setErrorMessage("Process interrupted");
            } catch (Exception e) {
                recipient.setStatus(EmailStatus.FAILED);
                recipient.setErrorMessage(e.getMessage());
            }

            recipientRepository.save(recipient);
        });
    }
}
