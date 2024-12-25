package com.smp.mail.service;

import com.smp.mail.dto.ItemDTO;
import com.smp.mail.dto.OrderDTO;
import com.smp.mail.entity.ItemEntity;
import com.smp.mail.entity.OrderEntity;
import com.smp.mail.entity.ServiceEntity;
import com.smp.mail.exception.*;
import com.smp.mail.repository.ItemRepository;

import com.smp.mail.repository.OrderRepository;
import com.smp.mail.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ItemRepository itemRepository;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Transactional
    public OrderEntity createOrder(OrderDTO orderDTO) {
        validateOrderDTO(orderDTO);

        try {
            Long mainServiceId = extractMainServiceId(orderDTO);
            ServiceEntity mainService = findMainService(mainServiceId);

            OrderEntity orderEntity = createOrderEntity(orderDTO, mainService);

            List<ItemEntity> itemEntities = createOrderItems(orderEntity, orderDTO);

            return saveOrderWithItems(orderEntity, itemEntities);

        } catch (ServiceNotFoundException ex) {
            throw new OrderItemValidationException(
                    "Одна из выбранных услуг не найдена: " + ex.getMessage(), ex);
        } catch (InvalidOrderDataException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new OrderCreationException("Не удалось создать заказ", ex);
        }
    }

    private void validateOrderDTO(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new InvalidOrderDataException("OrderDTO не может быть null");
        }
        validateEmail(orderDTO.getUserEmail());

        validateOrderItems(orderDTO.getItems());
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidOrderDataException("Email не может быть пустым");
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidOrderDataException("Некорректный формат email: " + email);
        }
    }
    private void validateOrderItems(List<ItemDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderDataException("Список услуг не может быть пустым");
        }

        Set<Long> uniqueServiceIds = new HashSet<>();
        for (ItemDTO item : items) {
            if (item.getServiceId() == null) {
                throw new OrderItemValidationException("ID услуги не может быть null");
            }

            if (!uniqueServiceIds.add(item.getServiceId())) {
                throw new OrderItemValidationException(
                        "Duplicate service ID: " + item.getServiceId()
                );
            }
        }
    }

    private Long extractMainServiceId(OrderDTO orderDTO) {
        return orderDTO.getItems().get(0).getServiceId();
    }

    private ServiceEntity findMainService(Long mainServiceId) {
        return serviceRepository
                .findById(mainServiceId)
                .orElseThrow(() -> new ServiceNotFoundException(
                        "Основной сервис с ID " + mainServiceId + " не найден"
                ));
    }

    private OrderEntity createOrderEntity(OrderDTO orderDTO, ServiceEntity mainService) {
        try {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setUserEmail(orderDTO.getUserEmail());
            orderEntity.setCreatedAt(LocalDateTime.now());
            orderEntity.setService(mainService);
            return orderEntity;
        } catch (RuntimeException ex) {
            throw new OrderCreationException("Не удалось создать сущность заказа", ex);
        }
    }

    private List<ItemEntity> createOrderItems(OrderEntity orderEntity, OrderDTO orderDTO) {
        List<ItemEntity> itemEntities = new ArrayList<>();

        for (ItemDTO itemDTO : orderDTO.getItems()) {
            ServiceEntity service = serviceRepository
                    .findById(itemDTO.getServiceId())
                    .orElseThrow(() -> new ServiceNotFoundException(
                            "Сервис с ID " + itemDTO.getServiceId() + " не найден"
                    ));

            try {
                ItemEntity itemEntity = new ItemEntity();
                itemEntity.setOrderEntity(orderEntity);
                itemEntity.setService(service);
                itemEntities.add(itemEntity);
            } catch (RuntimeException ex) {
                throw new OrderItemValidationException(
                        "Не удалось создать позицию заказа для услуги " + service.getName(), ex);
            }
        }
        return itemEntities;
    }

    private OrderEntity saveOrderWithItems(OrderEntity orderEntity, List<ItemEntity> itemEntities) {
        try {
            OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

            List<ItemEntity> savedItemEntities = itemRepository.saveAll(itemEntities);

            savedOrderEntity.setItemEntities(savedItemEntities);

            return savedOrderEntity;
        } catch (DataIntegrityViolationException ex) {
            throw new OrderPersistenceException(
                    "Нарушение целостности данных при сохранении заказа", ex);
        } catch (DataAccessException ex) {
            throw new ServiceDatabaseConnectionException(
                    "Ошибка подключения к базе данных при сохранении заказа", ex);
        } catch (RuntimeException ex) {
            throw new OrderPersistenceException(
                    "Не удалось сохранить заказ", ex);
        }
    }

    public OrderEntity findById(Long orderId) {
        if (orderId == null) {
            throw new InvalidOrderDataException("ID заказа не может быть null");
        }
        try {
            return orderRepository
                    .findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Заказ с ID " + orderId + " не найден"
                    ));
        } catch (RuntimeException ex) {
            throw new OrderPersistenceException(
                    "Ошибка при поиске заказа", ex);
        }
    }
}