package com.smp.mail.service;


import com.smp.mail.dto.ServiceDTO;
import com.smp.mail.dto.ServiceOrderRequestDTO;
import com.smp.mail.exception.InvalidServiceSelectionException;
import com.smp.mail.exception.ServiceDatabaseConnectionException;
import com.smp.mail.exception.ServiceNotFoundException;
import com.smp.mail.exception.ServiceRepositoryException;
import com.smp.mail.mapper.ServiceMapper;
import com.smp.mail.repository.ServiceRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ServiceOrderService {
    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final EmailService emailService;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";

    @Autowired
    public ServiceOrderService(
            ServiceRepository serviceRepository,
            ServiceMapper serviceMapper,
            EmailService emailService
    ) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.emailService = emailService;
    }

    public List<ServiceDTO> getAllServices() {
        try {
            return serviceMapper.toDtoList(serviceRepository.findAll());
        } catch (DataAccessResourceFailureException e) {
            throw new ServiceDatabaseConnectionException("Не удалось подключиться к базе данных", e);
        } catch (DataRetrievalFailureException e) {
            throw new ServiceRepositoryException("Ошибка при извлечении услуг", e);
        }
    }

    public void processServiceOrder(ServiceOrderRequestDTO orderRequest) {
        validateEmail(orderRequest.getUserEmail());

        var selectedServices = serviceRepository.findAllById(orderRequest.getSelectedServiceIds());

        if (selectedServices.isEmpty()) {
            throw new ServiceNotFoundException("Выбранные услуги не найдены");
        }

        Set<Long> uniqueServiceIds = new HashSet<>(orderRequest.getSelectedServiceIds());
        if (uniqueServiceIds.size() != orderRequest.getSelectedServiceIds().size()) {
            throw new InvalidServiceSelectionException("Обнаружены дублирующиеся услуги");
        }

        if (selectedServices.size() != orderRequest.getSelectedServiceIds().size()) {
            throw new InvalidServiceSelectionException("Некоторые выбранные услуги не существуют");
        }

        var selectedServiceDTOs = serviceMapper.toDtoList(selectedServices);

        emailService.sendOrderConfirmation(
                orderRequest.getUserEmail(),
                selectedServiceDTOs
        );
    }
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Некорректный формат email");
        }

        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Невалидный email адрес");
        }
    }
}