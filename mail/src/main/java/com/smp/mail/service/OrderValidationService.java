package com.smp.mail.service;

import com.smp.mail.dto.*;
import com.smp.mail.entity.ServiceEntity;
import com.smp.mail.exception.InvalidOrderDataException;
import com.smp.mail.exception.OrderItemValidationException;
import com.smp.mail.exception.ServiceNotFoundException;
import com.smp.mail.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class OrderValidationService {
    @Autowired
    private ServiceRepository serviceRepository;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String SERVICE_DETAILS_MISSING = "Отсутствуют детали для услуги: ";
    private static final String POSITIVE_VALUE_SUFFIX = " должно быть положительным числом для услуги: ";

    public void validateOrderDTO(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new InvalidOrderDataException("OrderDTO не может быть null");
        }

        validateEmail(orderDTO.getUserEmail());
        validateOrderItems(orderDTO.getItems());
        validateServiceSpecificDetails(orderDTO);
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidOrderDataException("Email не может быть пустым");
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidOrderDataException("Некорректный формат email: " + email);
        }
    }

    public void validateOrderItems(List<ItemDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderDataException("Список услуг не может быть пустым");
        }

        Set<Long> uniqueServiceIds = new HashSet<>();
        for (ItemDTO item : items) {
            if (item.getServiceId() == null) {
                throw new OrderItemValidationException("ID услуги не может быть null");
            }
        }
    }

    public void validateServiceSpecificDetails(OrderDTO orderDTO) {
        for (ItemDTO item : orderDTO.getItems()) {
            ServiceEntity service = serviceRepository
                    .findById(item.getServiceId())
                    .orElseThrow(() -> new ServiceNotFoundException(
                            "Сервис с ID " + item.getServiceId() + " не найден"
                    ));

            switch (service.getCode()) {
                case OrderService.CODE_CREDIT_CARD:
                    validateCreditCardDetails(item.getCreditCardDetails(), service.getName());
                    break;
                case OrderService.CODE_TRANSFER_FUNDS:
                    validateTransferFundsDetails(item.getTransferFundsDetails(), service.getName());
                    break;
                case OrderService.CODE_MORTGAGE:
                    validateMortgageDetails(item.getMortgageDetails(), service.getName());
                    break;
                default:
                    throw new OrderItemValidationException(
                            "Неподдерживаемый тип услуги: " + service.getCode()
                    );
            }
        }
    }

    public void validateCreditCardDetails(CreditCardDetailsDTO details, String serviceName) {
        if (details == null) {
            throw new OrderItemValidationException(
                    SERVICE_DETAILS_MISSING + serviceName
            );
        }

        validatePositiveBigDecimal(
                details.getCreditLimit(),
                "Кредитный лимит",
                serviceName
        );
        validatePositiveBigDecimal(
                details.getInterestRate(),
                "Процентная ставка",
                serviceName
        );
        validatePositiveInteger(
                details.getLoanTerm(),
                "Срок кредита",
                serviceName
        );
    }

    public void validateTransferFundsDetails(TransferFundsDetailsDTO details, String serviceName) {
        if (details == null) {
            throw new OrderItemValidationException(
                    SERVICE_DETAILS_MISSING + serviceName
            );
        }

        if (details.getTransferType() == null) {
            throw new OrderItemValidationException(
                    "Не указан тип перевода для услуги: " + serviceName
            );
        }

        validatePositiveBigDecimal(
                details.getTransferAmount(),
                "Сумма перевода",
                serviceName
        );
        validatePositiveBigDecimal(
                details.getCommission(),
                "Комиссия",
                serviceName
        );
    }

    public void validateMortgageDetails(MortgageDetailsDTO details, String serviceName) {
        if (details == null) {
            throw new OrderItemValidationException(
                    SERVICE_DETAILS_MISSING + serviceName
            );
        }

        validatePositiveBigDecimal(
                details.getLoanAmount(),
                "Сумма кредита",
                serviceName
        );
        validatePositiveBigDecimal(
                details.getInterestRate(),
                "Процентная ставка",
                serviceName
        );
        validatePositiveInteger(
                details.getLoanTerm(),
                "Срок кредита",
                serviceName
        );
    }

    public void validatePositiveBigDecimal(BigDecimal value, String fieldName, String serviceName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderItemValidationException(
                    fieldName + POSITIVE_VALUE_SUFFIX + serviceName
            );
        }
    }

    public void validatePositiveInteger(Integer value, String fieldName, String serviceName) {
        if (value == null || value <= 0) {
            throw new OrderItemValidationException(
                    fieldName + POSITIVE_VALUE_SUFFIX + serviceName
            );
        }
    }
}