package com.pranshudev.spendlytics.service;

import com.pranshudev.spendlytics.dto.ExpenseDTO;
import com.pranshudev.spendlytics.dto.ExpenseSummaryDTO;
import com.pranshudev.spendlytics.entity.ExpenseEntity;
import com.pranshudev.spendlytics.entity.ProfileEntity;
import com.pranshudev.spendlytics.repository.ExpenseRepository;
import com.pranshudev.spendlytics.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private static final DateTimeFormatter LOG_TIME_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy HH:mm:ss");
    private final ExpenseService expenseService;

    @Value("${spendlytics.frontend.url}")
    private String frontUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST") // 10 PM daily
//    @Scheduled(cron = "0 * *  * * * ", zone = "Asia/Kolkata")
    public void sendDailyIncomesExpenseReminder() {

        String startTime = LocalDateTime.now().format(LOG_TIME_FORMAT);
        log.info("🔔 Daily Reminder Job started on {}", startTime);

        List<ProfileEntity> profiles = profileRepository.findAll();
        log.info("Found {} profiles for daily reminder processing", profiles.size());

        int successCount = 0;
        int failureCount = 0;

        for (ProfileEntity profile : profiles) {

            if (Boolean.FALSE.equals(profile.getIsActive())) {
                log.debug("Skipping inactive profile: email={}", profile.getEmail());
                continue;
            }

            try {
                log.debug("Preparing reminder email for user: {}", profile.getEmail());

                String subject = "Quick reminder to log today’s income & expenses 💸";

                String body = """
                        <html>
                          <body style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px;">
                            <div style="max-width:600px;margin:auto;background:#fff;padding:24px;border-radius:8px;">
                              <h2>Hi %s 👋</h2>
                              <p>This is a friendly reminder to log today’s <b>income and expenses</b>.</p>
                              <a href="%s"
                                 style="background:#28a745;color:#fff;padding:12px 24px;
                                        text-decoration:none;border-radius:6px;display:inline-block;">
                                 Go to Dashboard
                              </a>
                              <p style="margin-top:20px;">
                                Best regards,<br/><b>Spendlytics Team</b>
                              </p>
                            </div>
                          </body>
                        </html>
                        """.formatted(
                        profile.getFullName(),
                        frontUrl + "/dashboard"
                );

                // ✅ ACTUAL EMAIL SEND
                emailService.sendHtmlEmail(
                        profile.getEmail(),
                        subject,
                        body
                );

                successCount++;
                log.info("Reminder email sent successfully to {}", profile.getEmail());

            } catch (Exception ex) {
                failureCount++;
                log.error(
                        "Failed to send reminder email to {}. Reason: {}",
                        profile.getEmail(),
                        ex.getMessage(),
                        ex
                );
            }
        }

        String endTime = LocalDateTime.now().format(LOG_TIME_FORMAT);
        log.info("✅ Daily Reminder Job completed on {}", endTime);
        log.info("Summary → Success: {}, Failed: {}", successCount, failureCount);





    }


     @Scheduled(cron = "0 55 23 * * *", zone = "Asia/Kolkata")


    public void sendDailyExpenseSummary() {

        String startTime = LocalDateTime.now().format(LOG_TIME_FORMAT);
        log.info("📊 Daily Expense Summary Job started on {}", startTime);

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {

            if (Boolean.FALSE.equals(profile.getIsActive())) {
                continue;
            }

            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59, 999_999_999);

            List<ExpenseSummaryDTO> expenses = expenseService
                    .getExpensesSummaryForProfileIdAndDate(profile.getId(), todayStart, todayEnd);



            if (expenses.isEmpty()) {
                log.debug("No expenses for user {} today, skipping email",
                        profile.getEmail());
                continue;
            }

            try {
                String subject = "Your Daily Expense Summary 📊";

                String body = buildDailySummaryEmail(profile, expenses);

                emailService.sendHtmlEmail(
                        profile.getEmail(),
                        subject,
                        body
                );

                log.info("Daily expense summary sent to {}", profile.getEmail());

            } catch (Exception ex) {
                log.error("Failed to send summary email to {}",
                        profile.getEmail(), ex);
            }
        }

        log.info("✅ Daily Expense Summary Job completed");
    }
    private String buildDailySummaryEmail(
            ProfileEntity profile,
            List<ExpenseSummaryDTO> expenses
    ) {

        String tableRows = buildExpenseTableRows(expenses);

        return """
    <html>
      <body style="font-family: Arial, sans-serif; background:#f4f6f8; padding:20px;">
        <div style="max-width:650px;margin:auto;background:#ffffff;
                    padding:24px;border-radius:8px;">

          <h2 style="color:#2c3e50;">Hi %s 👋</h2>

          <p style="color:#555;">
            Here’s a quick summary of your expenses for today 📊
          </p>

          <table style="width:100%%;border-collapse:collapse;margin-top:16px;">
            <thead>
              <tr style="background:#f1f3f5;">
                <th style="text-align:left;padding:8px;">Expense</th>
                <th style="text-align:left;padding:8px;">Category</th>
                <th style="text-align:right;padding:8px;">Amount</th>
                <th style="text-align:left;padding:8px;">Time</th>
              </tr>
            </thead>
            <tbody>
              %s
            </tbody>
          </table>

          <p style="margin-top:20px;color:#666;">
            Reviewing your daily spending helps you stay mindful
            and build better financial habits 💡
          </p>

          <p style="margin-top:24px;color:#444;">
            Wishing you a financially smart tomorrow 🌱<br/>
            <b>Best regards,</b><br/>
            <b>Spendlytics Team</b>
          </p>

        </div>
      </body>
    </html>
    """.formatted(
                profile.getFullName(),
                tableRows
        );
    }
    private String buildExpenseTableRows(List<ExpenseSummaryDTO> expenses) {

        StringBuilder rows = new StringBuilder();

        for (ExpenseSummaryDTO expense : expenses) {
            rows.append("""
            <tr>
                <td>%s</td>
                <td>%s</td>
                <td style="text-align:right; padding-right:15px;">₹%.2f</td>
                <td>%s</td>
            </tr>
        """.formatted(
                    expense.getName(),
                    expense.getCategoryName(),
                    expense.getAmount(),
                    expense.getTime()
            ));
        }

        return rows.toString();
    }

}