package com.smp.mail.service;

import com.smp.mail.dto.CreditCardDetailsDTO;
import com.smp.mail.dto.MortgageDetailsDTO;
import com.smp.mail.dto.ServiceDTO;
import com.smp.mail.dto.TransferFundsDetailsDTO;
import com.smp.mail.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.smp.mail.service.OrderService.*;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    private static final String EMAIL_ENCODING = "UTF-8";
    static final String HTML_START = "<html><body>";
    static final String HTML_END = "</body></html>";
    static final String LIST_START = "<ul>";
    static final String LIST_END = "</ul>";

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${email.to.admin}")
    private String adminEmail;

    public void sendOrderConfirmation(String userEmail, List<ServiceDTO> services) {
        try {
            if (userEmail == null || services == null) {
                throw new EmailSendingException("Email или список услуг не может быть null");
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setReplyTo(userEmail);
            helper.setSubject("Новый заказ услуг от " + userEmail);

            String htmlBody = buildEmailBody(userEmail, services);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Ошибка отправки email", e);
        }
    }
    private String buildEmailBody(String userEmail, List<ServiceDTO> services) {
        StringBuilder body = new StringBuilder();
        body.append(HTML_START);
        body.append("<h2>Новый заказ услуг</h2>");
        body.append("<p>От пользователя: ").append(userEmail).append("</p>");
        body.append("<h3>Заказанные услуги:</h3>");
        body.append(LIST_START);

        for (ServiceDTO service : services) {
            body.append("<li>")
                    .append(service.getName())
                    .append(" (Код: ").append(service.getCode())
                    .append(")</li>");

            switch (service.getCode()) {
                case CODE_CREDIT_CARD:
                    CreditCardDetailsDTO creditCardDetails = service.getCreditCardDetails();
                    if (creditCardDetails != null) {
                        body.append(LIST_START);
                        body.append("<li>Кредитный лимит: ").append(creditCardDetails.getCreditLimit()).append("</li>");
                        body.append("<li>Процентная ставка: ").append(creditCardDetails.getInterestRate()).append("%</li>");
                        body.append("<li>Срок кредита: ").append(creditCardDetails.getLoanTerm()).append(" мес.</li>");
                        body.append(LIST_END);
                    }
                    break;

                case CODE_TRANSFER_FUNDS:
                    TransferFundsDetailsDTO transferFundsDetails = service.getTransferFundsDetails();
                    if (transferFundsDetails != null) {
                        body.append(LIST_START);
                        body.append("<li>Тип перевода: ").append(transferFundsDetails.getTransferType()).append("</li>");
                        body.append("<li>Сумма перевода: ").append(transferFundsDetails.getTransferAmount()).append("</li>");
                        body.append("<li>Комиссия: ").append(transferFundsDetails.getCommission()).append("</li>");
                        body.append(LIST_END);
                    }
                    break;

                case CODE_MORTGAGE:
                    MortgageDetailsDTO mortgageDetails = service.getMortgageDetails();
                    if (mortgageDetails != null) {
                        body.append(LIST_START);
                        body.append("<li>Сумма кредита: ").append(mortgageDetails.getLoanAmount()).append("</li>");
                        body.append("<li>Срок кредита: ").append(mortgageDetails.getLoanTerm()).append(" мес.</li>");
                        body.append("<li>Процентная ставка: ").append(mortgageDetails.getInterestRate()).append("%</li>");
                        body.append(LIST_END);
                    }
                    break;
            }
        }

        body.append(LIST_END);
        body.append(HTML_END);
        return body.toString();
    }
}
