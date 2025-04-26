package com.app.stock.service;

import com.app.stock.dto.FolioDetail;
import com.app.stock.dto.FolioResponse;
import com.app.stock.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
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

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        StringBuilder content = new StringBuilder();

        content.append("<html>")
                .append("<body style='font-family:Arial, sans-serif; background-color:#f9f9f9; margin:0; padding:0;'>")
                .append("<table width='100%' style='max-width:600px; margin:auto; background-color:#ffffff; border:1px solid #ddd;'>")

                // Banner image row
                .append("<tr>")
                .append("<td style='text-align:center; padding:10px;'>")
                .append("<img src='cid:stockifyBanner' alt='Stockify Banner' style='width:100%; max-width:600px; height:auto;'/>")
                .append("</td>")
                .append("</tr>")

                // Email content row
                .append("<tr>")
                .append("<td style='padding:20px;'>")
                .append("<h2 style='color:#1976d2;'>Your Stock Portfolio</h2>")
                .append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse; width:100%;'>")
                .append("<tr style='background-color:#1976d2; color:#ffffff;'>")
                .append("<th>Ticker</th><th>Quantity</th><th>Price</th><th>Value</th>")
                .append("</tr>");

        for (FolioDetail detail : folio.getItems()) {
            content.append("<tr>")
                    .append("<td>").append(detail.getTicker()).append("</td>")
                    .append("<td>").append(detail.getQuantity()).append("</td>")
                    .append("<td>").append(currencyFormat.format(detail.getPrice())).append("</td>")
                    .append("<td>").append(currencyFormat.format(detail.getValue())).append("</td>")
                    .append("</tr>");
        }

        content.append("</table>")
                .append("<p style='margin-top:20px; font-size:16px;'><strong>Total Value: ")
                .append(currencyFormat.format(folio.getTotalValue()))
                .append("</strong></p>")
                .append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</body>")
                .append("</html>");

        helper.setText(content.toString(), true); // HTML content

        // Embed the banner image
        FileSystemResource banner = new FileSystemResource(new File("src/main/resources/stockify-banner.png"));
        helper.addInline("stockifyBanner", banner);

        mailSender.send(message);
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(String.format("Stockify <%s>", senderEmail));
            helper.setTo(toEmail);
            helper.setSubject("Verify your Stock Monitor Account");

            String content = "<html>" +
                    "<body style='font-family:Arial, sans-serif; background-color:#f9f9f9; margin:0; padding:0;'>" +
                    "  <table width='100%' style='max-width:600px; margin:auto; background-color:#ffffff; border:1px solid #ddd;'>" +
                    "    <tr>" +
                    "      <td style='text-align:center; padding:10px 0;'>" +
                    "        <img src='cid:stockifyBanner' alt='Stockify Banner' style='width:100%; max-width:600px; height:auto;'/>" +
                    "      </td>" +
                    "    </tr>" +
                    "    <tr>" +
                    "      <td style='padding:20px;'>" +
                    "        <p style='font-size:16px; color:#333;'>Thank you for registering with <strong>Stock Monitor</strong>!</p>" +
                    "        <p style='font-size:16px; color:#333;'>Your OTP for account verification is:</p>" +
                    "        <h2 style='color:#1976d2; font-size:28px;'>" + otp + "</h2>" +
                    "        <p style='font-size:16px; color:#333;'>Please enter this OTP in the app to verify your account.</p>" +
                    "      </td>" +
                    "    </tr>" +
                    "  </table>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);
            FileSystemResource banner = new FileSystemResource(new File("src/main/resources/stockify-banner.png"));
            helper.addInline("stockifyBanner", banner);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
