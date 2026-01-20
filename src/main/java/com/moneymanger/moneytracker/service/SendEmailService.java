package com.moneymanger.moneytracker.service;

import com.moneymanger.moneytracker.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String fromEmail;

    @Value("${brevo.sender.name:MoneyTracker}")
    private String senderName;  // Changed from fromName to avoid confusion

    private final RestTemplate restTemplate;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    /* ====================== SIMPLE HTML EMAIL ====================== */
    public void sendMail(String to, String subject, String htmlBody) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("❌ Brevo API key is not configured");
            return;
        }

        if (fromEmail == null || fromEmail.isEmpty()) {
            log.error("❌ Sender email is not configured");
            return;
        }

        log.info("Sending email to: {}, Subject: {}", to, subject);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("name", senderName, "email", fromEmail));
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("htmlContent", htmlBody);

        sendEmailRequest(payload);
    }

    /* ====================== EMAIL WITH ATTACHMENT ====================== */
    public void sendEmailWithAttachment(ProfileEntity profile, String subject,
                                        byte[] attachment, String fileName, boolean isIncome) {
        if (profile == null || profile.getEmail() == null) {
            log.error("❌ Profile or email is null");
            return;
        }

        log.info("Sending {} report to: {}, File: {}",
                isIncome ? "income" : "expense",
                profile.getEmail(), fileName);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("name", senderName, "email", fromEmail));
        payload.put("to", List.of(Map.of("email", profile.getEmail())));
        payload.put("subject", subject);
        payload.put("htmlContent", isIncome ?
                generateIncomeEmailHTML(profile) :
                generateExpenseEmailHTML(profile));

        if (attachment != null && attachment.length > 0 && fileName != null) {
            Map<String, Object> file = new HashMap<>();
            file.put("content", Base64.getMimeEncoder().encodeToString(attachment));
            file.put("name", fileName);

            // Set content type based on file extension
            String contentType = getContentType(fileName);
            if (contentType != null) {
                file.put("contentType", contentType);
            }

            payload.put("attachment", List.of(file));
        }

        sendEmailRequest(payload);
    }

    /* ====================== MAKE REST CALL TO BREVO ====================== */
    private void sendEmailRequest(Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            // Add accept header
            headers.set("accept", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            log.debug("Sending request to Brevo API");

            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email sent successfully. Response: {}", response.getBody());
            } else {
                log.error("❌ Failed to send email. Status: {}, Response: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("❌ Exception while sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /* ====================== HELPER METHOD ====================== */
    private String getContentType(String fileName) {
        if (fileName == null) return null;

        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (lowerName.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (lowerName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerName.endsWith(".csv")) {
            return "text/csv";
        } else if (lowerName.endsWith(".txt")) {
            return "text/plain";
        }
        return null;
    }

    // ... rest of your HTML methods remain the same
    private String generateIncomeEmailHTML(ProfileEntity profile) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body{font-family:Arial;background:#f7f7f7;margin:0;padding:0;}" +
                ".container{max-width:600px;margin:20px auto;background:#fff;padding:20px;border-radius:10px;box-shadow:0 4px 15px rgba(0,0,0,0.1);}" +
                "h1{color:#6D28D9;}p{color:#333;font-size:14px;line-height:1.5;}" +
                ".button{display:inline-block;padding:10px 20px;margin-top:20px;background:#6D28D9;color:white;text-decoration:none;border-radius:5px;}" +
                ".footer{margin-top:30px;font-size:12px;color:#888;text-align:center;}" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<h1>Income Report</h1>" +
                "<p>Hi " + (profile.getFullName() != null ? profile.getFullName() : "User") + ",</p>" +
                "<p>Attached is your latest income report in Excel format. You can review all your income transactions easily.</p>" +
                "<p>Thank you for using MoneyTracker!</p>" +
                "<a href='#' class='button'>Open Dashboard</a>" +
                "<div class='footer'>MoneyTracker &copy; " + java.time.Year.now() + " | All rights reserved</div>" +
                "</div></body></html>";
    }

    private String generateExpenseEmailHTML(ProfileEntity profile) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body{font-family:Arial;background:#f7f7f7;margin:0;padding:0;}" +
                ".container{max-width:600px;margin:20px auto;background:#fff;padding:20px;border-radius:10px;box-shadow:0 4px 15px rgba(0,0,0,0.1);}" +
                "h1{color:#EF4444;}p{color:#333;font-size:14px;line-height:1.5;}" +
                ".button{display:inline-block;padding:10px 20px;margin-top:20px;background:#EF4444;color:white;text-decoration:none;border-radius:5px;}" +
                ".footer{margin-top:30px;font-size:12px;color:#888;text-align:center;}" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<h1>Expense Report</h1>" +
                "<p>Hi " + (profile.getFullName() != null ? profile.getFullName() : "User") + ",</p>" +
                "<p>Attached is your latest expense report in Excel format. You can review all your expense transactions easily.</p>" +
                "<p>Thank you for using MoneyTracker!</p>" +
                "<a href='#' class='button'>Open Dashboard</a>" +
                "<div class='footer'>MoneyTracker &copy; " + java.time.Year.now() + " | All rights reserved</div>" +
                "</div></body></html>";
    }
}