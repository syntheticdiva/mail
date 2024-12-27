package com.smp.mail.service;

import com.smp.mail.dto.*;
import com.smp.mail.entity.*;
import com.smp.mail.enums.TransferType;
import com.smp.mail.exception.*;
import com.smp.mail.mapper.ServiceMapper;
import com.smp.mail.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.smp.mail.service.OrderProcessingService.ORDER_DATA_NULL;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemCreditCardRepository itemCreditCardRepository;

    @Autowired
    private ItemMortgageRepository itemMortgageRepository;

    @Autowired
    private ItemTransferFundsRepository itemTransferFundsRepository;

    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private OrderValidationService orderValidationService;
    public static final String CODE_CREDIT_CARD = "credit_card";
    public static final String CODE_TRANSFER_FUNDS = "transfer_funds";
    public static final String CODE_MORTGAGE = "mortgage";
    static final String ORDER_CREATION_FAILED = "Не удалось создать заказ";

    @Transactional
    public OrderEntity createOrder(OrderDTO orderDTO) {
        orderValidationService.validateOrderDTO(orderDTO);

        try {
            Long mainServiceId = extractMainServiceId(orderDTO);
            ServiceEntity mainService = findMainService(mainServiceId);

            OrderEntity orderEntity = createOrderEntity(orderDTO, mainService);

            List<ItemEntity> itemEntities = createOrderItems(orderEntity, orderDTO);

            OrderEntity savedOrder = saveOrderWithItems(orderEntity, itemEntities);

            saveItemSpecificDetails(savedOrder, orderDTO);

            return savedOrder;

        } catch (ServiceNotFoundException | InvalidOrderDataException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new OrderCreationException(ORDER_CREATION_FAILED, ex);
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
    private void saveItemSpecificDetails(OrderEntity savedOrder, OrderDTO orderDTO) {
        if (savedOrder == null || orderDTO == null) {
            throw new InvalidOrderDataException(ORDER_DATA_NULL);
        }

        for (int i = 0; i < savedOrder.getItemEntities().size(); i++) {
            try {
                ItemEntity itemEntity = savedOrder.getItemEntities().get(i);
                ItemDTO itemDTO = orderDTO.getItems().get(i);

                String serviceCode = itemEntity.getService().getCode();

                try {
                    switch (serviceCode) {
                        case CODE_CREDIT_CARD:
                            saveItemCreditCardDetails(itemEntity, itemDTO.getCreditCardDetails());
                            break;
                        case CODE_TRANSFER_FUNDS:
                            saveItemTransferFundsDetails(itemEntity, itemDTO.getTransferFundsDetails());
                            break;
                        case CODE_MORTGAGE:
                            saveItemMortgageDetails(itemEntity, itemDTO.getMortgageDetails());
                            break;
                    }
                } catch (DataIntegrityViolationException ex) {
                    throw new OrderPersistenceException(
                            "Ошибка сохранения деталей услуги: " + serviceCode, ex
                    );
                }
            } catch (RuntimeException ex) {
                throw new OrderCreationException(
                        "Ошибка при обработке позиции заказа #" + i, ex
                );
            }
        }
    }
    private void saveItemCreditCardDetails(ItemEntity itemEntity, CreditCardDetailsDTO details) {
        if (details != null) {
            ItemCreditCardEntity creditCardEntity = new ItemCreditCardEntity();
            creditCardEntity.setItem(itemEntity);
            creditCardEntity.setCreditLimit(details.getCreditLimit());
            creditCardEntity.setInterestRate(details.getInterestRate());
            creditCardEntity.setLoanTerm(details.getLoanTerm());

            itemCreditCardRepository.save(creditCardEntity);
        }
    }

    private void saveItemTransferFundsDetails(ItemEntity itemEntity, TransferFundsDetailsDTO details) {
        if (details != null) {
            ItemTransferFundsEntity transferFundsEntity = new ItemTransferFundsEntity();
            transferFundsEntity.setItem(itemEntity);
            transferFundsEntity.setTransferType(
                    details.getTransferType() != null
                            ? TransferType.valueOf(details.getTransferType())
                            : null
            );
            transferFundsEntity.setTransferAmount(details.getTransferAmount());
            transferFundsEntity.setCommission(details.getCommission());

            itemTransferFundsRepository.save(transferFundsEntity);
        }
    }

    private void saveItemMortgageDetails(ItemEntity itemEntity, MortgageDetailsDTO details) {
        if (details != null) {
            ItemMortgageEntity mortgageEntity = new ItemMortgageEntity();
            mortgageEntity.setItem(itemEntity);
            mortgageEntity.setLoanAmount(details.getLoanAmount());
            mortgageEntity.setLoanTerm(details.getLoanTerm());
            mortgageEntity.setInterestRate(details.getInterestRate());

            itemMortgageRepository.save(mortgageEntity);
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
