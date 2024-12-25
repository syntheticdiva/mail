package com.smp.mail.service;

import com.smp.mail.dto.ItemDTO;
import com.smp.mail.dto.OrderDTO;
import com.smp.mail.entity.ItemEntity;
import com.smp.mail.entity.OrderEntity;
import com.smp.mail.entity.ServiceEntity;
import com.smp.mail.exception.ServiceNotFoundException;
import com.smp.mail.repository.ItemRepository;

import com.smp.mail.repository.OrderRepository;
import com.smp.mail.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList; import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public OrderEntity createOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new IllegalArgumentException("OrderDTO не может быть null");
        }

        if (orderDTO.getUserEmail() == null || orderDTO.getUserEmail().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Список услуг не может быть пустым");
        }

        Long mainServiceId = orderDTO.getItems().get(0).getServiceId();

        ServiceEntity mainService = serviceRepository
                .findById(mainServiceId)
                .orElseThrow(() -> new ServiceNotFoundException("Основной сервис не найден"));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserEmail(orderDTO.getUserEmail());
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setService(mainService);

        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        List<ItemEntity> itemEntities = new ArrayList<>();
        for (ItemDTO itemDTO : orderDTO.getItems()) {
            ServiceEntity service = serviceRepository
                    .findById(itemDTO.getServiceId())
                    .orElseThrow(() -> new ServiceNotFoundException("Сервис не найден"));

            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setOrderEntity(savedOrderEntity);
            itemEntity.setService(service);

            itemEntities.add(itemEntity);
        }

        itemEntities = itemRepository.saveAll(itemEntities);

        savedOrderEntity.setItemEntities(itemEntities);

        return savedOrderEntity;
    }

    public OrderEntity findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ не найден"));
    }
}