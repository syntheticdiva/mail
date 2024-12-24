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
    private static final String CHARSET_UTF_8 = "UTF-8";

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${email.to.admin}")
    private String adminEmail;

    public void sendOrderConfirmation(String userEmail, List<ServiceDTO> services) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new EmailSendingException("Email не может быть пустым");
        }

        if (services == null || services.isEmpty()) {
            throw new EmailSendingException("Список услуг пуст");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, CHARSET_UTF_8);

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setReplyTo(userEmail);
            helper.setSubject("Новый заказ услуг от " + userEmail);

            String htmlBody = buildEmailBody(userEmail, services);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Не удалось отправить email");
        }
    }

    private String buildEmailBody(String userEmail, List<ServiceDTO> services) {
        StringBuilder body = new StringBuilder();
        body.append("<html><body>");
        body.append("<h2>Новый заказ услуг</h2>");
        body.append("<p>От пользователя: ").append(userEmail).append("</p>");
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