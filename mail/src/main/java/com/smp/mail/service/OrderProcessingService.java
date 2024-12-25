package com.smp.mail.service;

import com.smp.mail.dto.OrderDTO;
import com.smp.mail.dto.ServiceDTO;
import com.smp.mail.entity.OrderEntity;
import com.smp.mail.entity.ServiceEntity;
import com.smp.mail.exception.*;
import com.smp.mail.mapper.ServiceMapper;
import com.smp.mail.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderProcessingService {
    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceMapper serviceMapper;

    public Map<String, Object> saveOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new InvalidOrderDataException("Данные заказа не могут быть null");
        }

        try {
            OrderEntity savedOrder = orderService.createOrder(orderDTO);

            if (savedOrder == null) {
                throw new OrderCreationException("Не удалось создать заказ");
            }

            if (savedOrder.getItemEntities() == null || savedOrder.getItemEntities().isEmpty()) {
                throw new OrderCreationException("В заказе отсутствуют услуги");
            }

            List<ServiceDTO> serviceDTOs = savedOrder.getItemEntities().stream()
                    .map(item -> {
                        if (item == null || item.getService() == null) {
                            throw new OrderCreationException("Некорректная позиция заказа");
                        }
                        return serviceMapper.itemEntityToServiceDTO(item);
                    })
                    .collect(Collectors.toList());

            if (savedOrder.getUserEmail() == null || savedOrder.getUserEmail().trim().isEmpty()) {
                throw new InvalidOrderDataException("Email пользователя не может быть пустым");
            }

            emailService.sendOrderConfirmation(
                    savedOrder.getUserEmail(),
                    serviceDTOs
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedOrder.getId());
            response.put("email", savedOrder.getUserEmail());

            return response;

        } catch (InvalidOrderDataException |
                 OrderItemValidationException |
                 ServiceNotFoundException |
                 OrderCreationException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new OrderCreationException("Ошибка при работе с базой данных", ex);
        } catch (IllegalArgumentException |
                 IllegalStateException |
                 NullPointerException ex) {
            throw new OrderCreationException("Некорректные параметры при создании заказа", ex);
        } catch (RuntimeException ex) {
            throw new OrderCreationException("Непредвиденная ошибка при создании заказа", ex);
        }
    }

    public List<ServiceEntity> getAllServices() {
        try {
            List<ServiceEntity> services = serviceRepository.findAll();

            if (services.isEmpty()) {
                throw new ServiceRetrievalException("Список услуг пуст");
            }

            return services;
        } catch (DataAccessException ex) {
            throw new ServiceDatabaseConnectionException(
                    "Ошибка получения списка услуг", ex);
        }
    }
}