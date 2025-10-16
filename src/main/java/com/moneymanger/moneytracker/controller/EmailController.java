package com.moneymanger.moneytracker.controller;

import com.moneymanger.moneytracker.entity.ProfileEntity;
import com.moneymanger.moneytracker.service.ExcelService;
import com.moneymanger.moneytracker.service.SendEmailService;
import com.moneymanger.moneytracker.service.IncomeService;
import com.moneymanger.moneytracker.service.ExpenseService;
import com.moneymanger.moneytracker.service.ProfileService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final ProfileService profileService;
    private final SendEmailService sendEmailService;
    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    /* ====================== SEND PLAIN TEXT EMAIL ====================== */
    @GetMapping("/test/plain")
    public ResponseEntity<Void> sendPlainTextEmail() {
        ProfileEntity profile = profileService.getCurrentProfile();
        sendEmailService.sendMail(profile.getEmail(), "Test Email", "This is a plain text test email.");
        return ResponseEntity.ok().build();
    }


    @GetMapping("/income/excel")
    public ResponseEntity<Void> sendIncomeHtmlEmail() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();

        // Generate Excel
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(outputStream, incomeService.getAllIcomesforCurrentUser());

        // Send HTML email with attachment
        sendEmailService.sendEmailWithAttachment(profile,
                "Income Report - MoneyTracker",
                outputStream.toByteArray(),
                "income.xlsx",
                true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expense/excel")
    public ResponseEntity<Void> sendExpenseHtmlEmail() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();

        // Generate Excel
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(outputStream, expenseService.getAllexpensesforCurrentUser());

        // Send HTML email with attachment
        sendEmailService.sendEmailWithAttachment(profile,
                "Expense Report - MoneyTracker",
                outputStream.toByteArray(),
                "expense.xlsx",
                false);
        return ResponseEntity.ok().build();
    }
}
