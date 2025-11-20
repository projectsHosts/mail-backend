package mail.com.controller;

import mail.com.dto.EmailResponse;
import mail.com.entity.EmailCampaign;
import mail.com.entity.EmailRecipient;
import mail.com.loginDetails.model.User;
import mail.com.loginDetails.repo.UserRepository;
import mail.com.repository.EmailCampaignRepository;
import mail.com.repository.EmailRecipientRepository;
import mail.com.service.BulkEmailService;
import mail.com.service.ExcelProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailCampaignRepository campaignRepository;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private EmailRecipientRepository recipientRepository;

    @Autowired
    private ExcelProcessingService excelService;

    @Autowired
    private BulkEmailService bulkEmailService;

    // FIXED: Using consumes attribute and proper parameter names
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadExcel(
            @RequestPart("file") MultipartFile file,  // Changed to @RequestPart
            @RequestParam("name") String campaignName,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body,
            @RequestParam(value = "delaySeconds", defaultValue = "0") Integer delaySeconds,
            @RequestParam("getCreatedBy") String createdBy,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) {

        try {

            // Check if file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Check file type
            String contentType = file.getContentType();
            if (!isExcelFile(contentType)) {
                return ResponseEntity.badRequest().body("Please upload an Excel file (.xlsx or .xls)");
            }

            // Process Excel file
            List<EmailRecipient> recipients = excelService.processExcelFile(file);

            if (recipients == null || recipients.isEmpty()) {
                return ResponseEntity.badRequest().body("No valid email addresses found in the file");
            }

            // Create campaign
            EmailCampaign campaign = new EmailCampaign();
            campaign.setName(campaignName);
            campaign.setSubject(subject);
            campaign.setBody(body);
            campaign.setDelaySeconds(delaySeconds);
            User user = userRepository.findByEmail(createdBy)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            campaign.setCreatedBy(user);

            // handle attachment optional
            if (attachment != null && !attachment.isEmpty()) {
                // validate type & size
                String attType = attachment.getContentType();
                if (!isAllowedAttachment(attType)) {
                    return ResponseEntity.badRequest().body("Attachment must be PDF/DOC/DOCX");
                }
                // save file to disk (or S3) and set path
                String savedPath = saveAttachmentToDisk(attachment);
                campaign.setAttachmentPath(savedPath);
            }

            // Associate recipients with campaign
            for (EmailRecipient recipient : recipients) {
                recipient.setCampaign(campaign);
            }
            campaign.setRecipients(recipients);

            // Save to database
            EmailCampaign savedCampaign = campaignRepository.save(campaign);

            // Return JSON instead of plain text
            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format(
                    "Campaign '%s' created successfully with %d recipients",
                    campaignName, recipients.size()
            ));
            response.put("campaignId", savedCampaign.getId());

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }

    private boolean isExcelFile(String contentType) {
        return contentType != null &&
                (contentType.equals("application/vnd.ms-excel") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    // ... rest of the methods remain same
    @PostMapping("/campaign/{campaignId}/send")
    public ResponseEntity<?> sendCampaign(@PathVariable Long campaignId, Principal principal) {
        try {
            System.out.println("Send campaign called for ID: " + campaignId);

            EmailCampaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> new RuntimeException("Campaign not found"));

//            String fromEmail = principal.getName();

//            System.out.println("Check here..... "+fromEmail);
            // ✅ ACTUALLY SEND EMAILS - ADD THIS LINE
            bulkEmailService.sendBulkEmails(campaign);

            // ✅ Return JSON (NOT plain text)
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email sending started successfully!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @GetMapping("/campaign/{campaignId}/recipients")
    public ResponseEntity<List<EmailResponse>> getRecipients(@PathVariable Long campaignId) {
        try {
            List<EmailRecipient> recipients = recipientRepository.findByCampaignId(campaignId);

            List<EmailResponse> response = recipients.stream().map(recipient -> {
                EmailResponse emailResponse = new EmailResponse();
                emailResponse.setId(recipient.getId());
                emailResponse.setEmail(recipient.getEmail());
                emailResponse.setName(recipient.getName());
                emailResponse.setStatus(recipient.getStatus().toString());
                emailResponse.setErrorMessage(recipient.getErrorMessage());
                emailResponse.setSentAt(recipient.getSentAt());
                return emailResponse;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

//    @GetMapping("/campaigns")
//    public ResponseEntity<List<EmailCampaign>> getCampaigns() {
//        try {
//            List<EmailCampaign> campaigns = campaignRepository.findAll();
//            return ResponseEntity.ok(campaigns);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().build();
//        }
//    }

//    @GetMapping("/campaigns")
//    public List<EmailCampaign> getCampaigns() {
//        return campaignRepository.findAllByOrderByCreatedAtDesc();
//    }

    @GetMapping("/campaigns")
    public ResponseEntity<?> getCampaigns(@RequestParam("email") String email) {
        List<EmailCampaign> campaigns =
                campaignRepository.findByCreatedByEmailOrderByCreatedAtDesc(email);

        return ResponseEntity.ok(campaigns);
    }



    private boolean isAllowedAttachment(String contentType) {
        return contentType != null && (contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }

    private String saveAttachmentToDisk(MultipartFile file) throws IOException {
        // choose folder (configurable)
        String uploadsDir = "/var/app/uploads/attachments"; // or use application.properties path
        Files.createDirectories(Paths.get(uploadsDir));

        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID().toString() + ext;
        Path dest = Paths.get(uploadsDir, filename);
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        }
        return dest.toString(); // store absolute path or relative path as needed
    }

}