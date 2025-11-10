package mail.com.repository;

import mail.com.entity.EmailRecipient;
import mail.com.entity.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRecipientRepository extends JpaRepository<EmailRecipient,Long> {
    List<EmailRecipient> findByCampaignIdAndStatus(Long campaignId, EmailStatus status);
    List<EmailRecipient> findByCampaignId(Long campaignId);
}
