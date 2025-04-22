package com.app.stock.service;

import com.app.stock.dto.FolioDetail;
import com.app.stock.dto.FolioResponse;
import com.app.stock.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendFolioEmail(User user, FolioResponse folio) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

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
}
