package com.moneymanger.moneytracker.service;

import com.moneymanger.moneytracker.entity.ProfileEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    /* ====================== NORMAL EMAIL ====================== */
    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        log.info("Plain text email sent to {}", to);
    }

//    public void sendHtmlMail(String to, String subject, String htmlBody) throws MessagingException, UnsupportedEncodingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true = multipart, UTF-8 for emojis
//
//        helper.setFrom(fromEmail, "MoneyManager"); // professional sender name
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(htmlBody, true); // true = HTML content
//
//        mailSender.send(message);
//        log.info("HTML email sent to {}", to);
//    }



    /* ====================== ATTACHMENT EMAIL ====================== */
    public void sendEmailWithAttachemnt(String to, String subject, String body, byte[] attachment, String fileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body); // plain text body
        helper.addAttachment(fileName, new ByteArrayResource(attachment));

        mailSender.send(message);
        log.info("Email with attachment sent to {} as {}", to, fileName);
    }

    public void sendHtmlMail(String to, String subject, String htmlBody) throws MessagingException, UnsupportedEncodingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "MoneyManager");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("HTML email sent successfully to {}", to);

        } catch (Exception e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
            throw e; // Re-throw to see the actual error
        }
    }

    /* ====================== FANCY HTML EMAIL WITH ATTACHMENT ====================== */
    public void sendHtmlEmailWithAttachment(ProfileEntity profile, String subject, byte[] attachment, String fileName, boolean isIncome) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlBody = isIncome ? generateIncomeEmailHTML(profile) : generateExpenseEmailHTML(profile);

        helper.setFrom(fromEmail, "MoneyTracker");
        helper.setTo(profile.getEmail());
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // HTML content
        helper.addAttachment(fileName, new ByteArrayResource(attachment));

        mailSender.send(message);
        log.info("HTML email sent to {} with attachment {}", profile.getEmail(), fileName);
    }

    /* ====================== HTML TEMPLATE FOR INCOME ====================== */
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

    /* ====================== HTML TEMPLATE FOR EXPENSE ====================== */
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
