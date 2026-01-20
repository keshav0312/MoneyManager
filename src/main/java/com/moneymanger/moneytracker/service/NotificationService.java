package com.moneymanger.moneytracker.service;

import com.moneymanger.moneytracker.dto.ExpenseDTO;
import com.moneymanger.moneytracker.entity.ProfileEntity;
import com.moneymanger.moneytracker.reposistory.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final ExpenseService expenseService;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;
    private final SendEmailService sendEmailService;

   @Value("${money.manager.frontend.url}")
    private String frontendUrl;


  @Scheduled(cron = "${cron.daily.remainder.email.time}", zone = "Asia/Kolkata")
    public void sendDailyExpensesRemainder() {
        log.info("Job started: sendDailyExpensesRemainder");

        List<ProfileEntity> profileEntities = profileRepository.findAll();

        log.info(profileEntities.toString());

        for (ProfileEntity profileEntity : profileEntities) {
            String email = profileEntity.getEmail();
            String subject = "💰 Daily Expense Reminder - MoneyManager";

            String body = "<!DOCTYPE html>"
                    + "<html lang='en'>"
                    + "<head>"
                    + "<meta charset='UTF-8'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Daily Expense Reminder</title>"
                    + "<style>"
                    + "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7fb; margin: 0; padding: 0; }"
                    + ".container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); overflow: hidden; }"
                    + ".header { background: linear-gradient(135deg, #4facfe, #00f2fe); color: #fff; text-align: center; padding: 25px 10px; }"
                    + ".header h1 { margin: 0; font-size: 28px; }"
                    + ".content { padding: 25px 30px; color: #333; text-align: center; }"
                    + ".content p { font-size: 16px; line-height: 1.6; }"
                    + ".button { display: inline-block; margin-top: 25px; padding: 12px 25px; background: linear-gradient(135deg, #43e97b, #38f9d7); color: white; text-decoration: none; border-radius: 30px; font-size: 16px; font-weight: bold; transition: 0.3s ease; }"
                    + ".button:hover { background: linear-gradient(135deg, #36d1dc, #5b86e5); transform: translateY(-2px); }"
                    + ".footer { background-color: #f1f1f1; text-align: center; padding: 15px; font-size: 14px; color: #555; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "  <div class='header'>"
                    + "      <h1>MoneyManager</h1>"
                    + "  </div>"
                    + "  <div class='content'>"
                    + "      <p>Hi <strong>" + profileEntity.getFullName() + "</strong>, 👋</p>"
                    + "      <p>This is your friendly reminder to <strong>log today’s expenses</strong> in your MoneyManager account.</p>"
                    + "      <p>Tracking your daily spending helps you stay financially healthy and meet your savings goals 💡</p>"
                    + "      <a class='button' href='" + frontendUrl +"/login" +"' target='_blank'>View My Dashboard</a>"
                    + "  </div>"
                    + "  <div class='footer'>"
                    + "      <p>© 2025 MoneyManager | Stay on top of your expenses 💸</p>"
                    + "  </div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            // Send the email
            sendEmailService.sendMail(email, subject, body);
        }

        log.info("Job completed: Daily expense reminder emails sent successfully!");
    }
    // Scheduled to run every day at 11:00 PM IST
 @Scheduled(cron = "${cron.daily.summary.email.time}", zone = "Asia/Kolkata")
 @Transactional(readOnly = true)
    public void sendDailyExpensesSummary() {
        log.info("Job started: sendDailyExpensesSummary");

        List<ProfileEntity> profileEntities = profileRepository.findAll();

        for (ProfileEntity profileEntity : profileEntities) {
            String email = profileEntity.getEmail();
            String subject = "📊 Your Daily Expense Summary - MoneyManager";

            List<ExpenseDTO> expenseDTOS = expenseService.filterExpensesForToday(profileEntity.getId(), LocalDate.now());

            StringBuilder tableRows = new StringBuilder();

            if (expenseDTOS.isEmpty()) {
                tableRows.append("<tr><td colspan='4' style='text-align:center; padding:15px;'>")
                        .append("No expenses recorded today 🎉")
                        .append("</td></tr>");
            } else {
                int i = 1;
                for (ExpenseDTO expense : expenseDTOS) {
                    tableRows.append("<tr>")
                            .append("<td>").append(i++).append("</td>")
                            .append("<td>").append(expense.getName()).append("</td>")
                            .append("<td>₹").append(expense.getAmount()).append("</td>")
                            .append("<td>").append(expense.getDate()).append("</td>")
                            .append("</tr>");
                }
            }

            String body = "<!DOCTYPE html>"
                    + "<html lang='en'>"
                    + "<head>"
                    + "<meta charset='UTF-8'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Daily Expense Summary</title>"
                    + "<style>"
                    + "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f6f9fc; margin: 0; padding: 0; }"
                    + ".container { max-width: 650px; margin: 40px auto; background: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); overflow: hidden; }"
                    + ".header { background: linear-gradient(135deg, #6a11cb, #2575fc); color: #fff; text-align: center; padding: 25px 15px; }"
                    + ".header h1 { margin: 0; font-size: 26px; }"
                    + ".content { padding: 25px 30px; color: #333; text-align: center; }"
                    + ".content p { font-size: 16px; line-height: 1.6; margin-bottom: 25px; }"
                    + "table { width: 100%; border-collapse: collapse; margin-bottom: 25px; }"
                    + "th, td { border: 1px solid #ddd; padding: 12px; text-align: center; }"
                    + "th { background: #2575fc; color: white; font-size: 15px; }"
                    + "tr:nth-child(even) { background-color: #f9f9f9; }"
                    + "tr:hover { background-color: #eef4ff; }"
                    + ".total { text-align: right; font-weight: bold; font-size: 16px; margin-top: 10px; }"
                    + ".button { display: inline-block; padding: 12px 25px; background: linear-gradient(135deg, #43e97b, #38f9d7); color: white; text-decoration: none; border-radius: 30px; font-size: 16px; font-weight: bold; transition: 0.3s; }"
                    + ".button:hover { background: linear-gradient(135deg, #36d1dc, #5b86e5); transform: translateY(-2px); }"
                    + ".footer { background-color: #f1f1f1; text-align: center; padding: 15px; font-size: 14px; color: #555; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "  <div class='header'>"
                    + "      <h1>MoneyManager</h1>"
                    + "      <p>Your Daily Expense Summary 💼</p>"
                    + "  </div>"
                    + "  <div class='content'>"
                    + "      <p>Hi <strong>" + profileEntity.getFullName() + "</strong>,</p>"
                    + "      <p>Here’s a quick look at your expenses for <strong>" + LocalDate.now() + "</strong>.</p>"
                    + "      <table>"
                    + "          <thead>"
                    + "              <tr>"
                    + "                  <th>#</th>"
                    + "                  <th>Name</th>"
                    + "                  <th>Amount</th>"
                    + "                  <th>Date</th>"
                    + "              </tr>"
                    + "          </thead>"
                    + "          <tbody>"
                    +               tableRows
                    + "          </tbody>"
                    + "      </table>";

            // If expenses exist, add total
            if (!expenseDTOS.isEmpty()) {
                double totalAmount = expenseDTOS.stream()
                        .mapToDouble(expense -> expense.getAmount().doubleValue())
                        .sum();

                body += "<p class='total'>Total Spent Today: <strong>₹" + totalAmount + "</strong></p>";
            }

            body += "      <a class='button' href='" + frontendUrl +"/login"+ "' target='_blank'>View My Dashboard</a>"
                    + "  </div>"
                    + "  <div class='footer'>"
                    + "      <p>© 2025 MoneyManager | Smarter way to track your money 💸</p>"
                    + "  </div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            sendEmailService.sendMail(email, subject, body);
        }

        log.info("Job completed: Daily expense summary emails sent successfully!");
    }

//  @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
//  public void testingMail() {
//      try {
//          sendEmailService.sendMail("keshavchoudhary719@gmail.com", "this is just testing mail", "test the mail working or not thanks for visiting agian..");
//          log.info("testing mail send successfully!");
//      } catch (Exception ex) {
//          log.info("testing mail send failed!");
//      }
//
//  }
}

