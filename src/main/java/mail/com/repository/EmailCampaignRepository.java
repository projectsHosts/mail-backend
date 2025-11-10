package mail.com.repository;

import mail.com.entity.EmailCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailCampaignRepository extends JpaRepository<EmailCampaign,Long> {
    List<EmailCampaign> findAllByOrderByCreatedAtDesc();
}
