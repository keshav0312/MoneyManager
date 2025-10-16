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

    @Value("${brevo.api.key}")  // Use environment variables
    private String apiKey;

    @Value("${brevo.sender.email}")  // Use environment variables
    private String fromEmail;

    @Value("${FROM_NAME:MoneyTracker}")
    private String fromName;

    private final RestTemplate restTemplate;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    /* ====================== SIMPLE HTML EMAIL ====================== */
    public void sendMail(String to, String subject, String htmlBody) {
        log.info("Sending email to: {}", to);
        log.info("Sender email: {}", fromEmail);

        // Don't log the API key!
        log.debug("API Key present: {}", apiKey != null ? "Yes" : "No");

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("name", fromName, "email", fromEmail));
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("htmlContent", htmlBody);

        sendEmailRequest(payload);
    }

    /* ====================== EMAIL WITH ATTACHMENT ====================== */
    public void sendEmailWithAttachment(ProfileEntity profile, String subject, byte[] attachment, String fileName, boolean isIncome) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("name", fromName, "email", fromEmail));
        payload.put("to", List.of(Map.of("email", profile.getEmail())));
        payload.put("subject", subject);
        payload.put("htmlContent", isIncome ? generateIncomeEmailHTML(profile) : generateExpenseEmailHTML(profile));

        if (attachment != null && attachment.length > 0) {
            Map<String, Object> file = new HashMap<>();
            file.put("content", Base64.getEncoder().encodeToString(attachment));
            file.put("name", fileName);
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

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            log.info("Sending request to Brevo API for: {}", payload.get("to"));

            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email sent successfully to: {}", payload.get("to"));
            } else {
                log.error("❌ Failed to send email. Status: {}, Response: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("❌ Exception while sending email to {}: {}",
                    payload.get("to"), e.getMessage());
            // Log the full error for debugging (remove in production)
            log.debug("Full error details:", e);
        }
    }

    // ... rest of your HTML template methods remain the same
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
                "<p>Hi " + profile.getFullName() + ",</p>" +
                "<p>Attached is your latest income report in Excel format. You can review all your income transactions easily.</p>" +
                "<p>Thank you for using MoneyTracker!</p>" +
                "<a href='#' class='button'>Open Dashboard</a>" +
                "<div class='footer'>MoneyTracker &copy; 2025 | All rights reserved</div>" +
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
                "<p>Hi " + profile.getFullName() + ",</p>" +
                "<p>Attached is your latest expense report in Excel format. You can review all your expense transactions easily.</p>" +
                "<p>Thank you for using MoneyTracker!</p>" +
                "<a href='#' class='button'>Open Dashboard</a>" +
                "<div class='footer'>MoneyTracker &copy; 2025 | All rights reserved</div>" +
                "</div></body></html>";
    }
}