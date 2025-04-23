package com.app.stock.service;

import com.app.stock.dto.FolioDetail;
import com.app.stock.dto.FolioResponse;
import com.app.stock.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendFolioEmail(User user, FolioResponse folio) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(String.format("Stockify <%s>", senderEmail));
        helper.setTo(user.getEmail());
        helper.setSubject("Your Stock Folio Summary");

        StringBuilder content = new StringBuilder("<h2>Your Stock Portfolio</h2><table border='1' style='border-collapse: collapse;'>");
        content.append("<tr><th>Ticker</th><th>Quantity</th><th>Price</th><th>Value</th></tr>");

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        for (FolioDetail detail : folio.getItems()) {
            content.append("<tr>")
                    .append("<td>").append(detail.getTicker()).append("</td>")
                    .append("<td>").append(detail.getQuantity()).append("</td>")
                    .append("<td>").append(currencyFormat.format(detail.getPrice())).append("</td>")
                    .append("<td>").append(currencyFormat.format(detail.getValue())).append("</td>")
                    .append("</tr>");
        }

        content.append("</table>");
        content.append("<p><strong>Total Value: ")
                .append(currencyFormat.format(folio.getTotalValue()))
                .append("</strong></p>");

        helper.setText(content.toString(), true); // true = HTML

        mailSender.send(message);
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(String.format("Stockify <%s>", senderEmail));
            helper.setTo(toEmail);
            helper.setSubject("Verify your Stock Monitor Account");

            String content = "<p>Thank you for registering with Stock Monitor!</p>"
                    + "<p>Your OTP for account verification is:</p>"
                    + "<h2 style='color:blue;'>" + otp + "</h2>"
                    + "<p>Please enter this OTP in the app to verify your account.</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
