package com.smp.mail.service;

import com.smp.mail.dto.ServiceDTO;
import com.smp.mail.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderConfirmation(String userEmail, List<ServiceDTO> services) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(userEmail);
            helper.setSubject("Подтверждение заказа");

            String htmlBody = buildEmailBody(services);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка отправки email", e);
        }
    }

    private String buildEmailBody(List<ServiceDTO> services) {
        StringBuilder body = new StringBuilder();
        body.append("<html><body>");
        body.append("<h2>Ваш заказ подтвержден</h2>");
        body.append("<h3>Заказанные услуги:</h3>");
        body.append("<ul>");

        for (ServiceDTO service : services) {
            body.append("<li>")
                    .append(service.getName())
                    .append(" (Код: ").append(service.getCode())
                    .append(")</li>");
        }

        body.append("</ul>");
        body.append("</body></html>");
        return body.toString();
    }
}