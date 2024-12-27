package com.smp.mail.service;

import com.smp.mail.dto.*;
import com.smp.mail.entity.*;
import com.smp.mail.exception.*;
import com.smp.mail.mapper.ServiceMapper;
import com.smp.mail.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.smp.mail.service.OrderService.*;

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
    @Autowired
    private ItemCreditCardRepository itemCreditCardRepository;
    @Autowired
    private ItemTransferFundsRepository itemTransferFundsRepository;
    @Autowired
    private ItemMortgageRepository itemMortgageRepository;
    static final String ORDER_DATA_NULL = "Данные заказа не могут быть null";

    public Map<String, Object> saveOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new InvalidOrderDataException(ORDER_DATA_NULL);
        }

        try {
            OrderEntity savedOrder = orderService.createOrder(orderDTO);

            if (savedOrder == null) {
                throw new OrderCreationException(ORDER_CREATION_FAILED);
            }

            if (savedOrder.getItemEntities() == null || savedOrder.getItemEntities().isEmpty()) {
                throw new OrderCreationException("В заказе отсутствуют услуги");
            }

            List<ServiceDTO> serviceDTOs = savedOrder.getItemEntities().stream()
                    .map(item -> {
                        if (item == null || item.getService() == null) {
                            throw new OrderCreationException("Некорректная позиция заказа");
                        }
                        ServiceDTO serviceDTO = serviceMapper.itemEntityToServiceDTO(item);
                        fillServiceSpecificDetails(item, serviceDTO);
                        return serviceDTO;
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

        } catch (InvalidOrderDataException | OrderItemValidationException | ServiceNotFoundException | OrderCreationException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new OrderCreationException("Ошибка при работе с базой данных", ex);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException ex) {
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
    private void fillServiceSpecificDetails(ItemEntity itemEntity, ServiceDTO serviceDTO) {
        String serviceCode = itemEntity.getService().getCode();

        switch (serviceCode) {
            case CODE_CREDIT_CARD:
                ItemCreditCardEntity creditCardEntity = itemCreditCardRepository.findByItem(itemEntity);
                if (creditCardEntity != null) {
                    CreditCardDetailsDTO details = new CreditCardDetailsDTO();
                    details.setCreditLimit(creditCardEntity.getCreditLimit());
                    details.setInterestRate(creditCardEntity.getInterestRate());
                    details.setLoanTerm(creditCardEntity.getLoanTerm());
                    serviceDTO.setCreditCardDetails(details);
                }
                break;

            case CODE_TRANSFER_FUNDS:
                ItemTransferFundsEntity transferEntity = itemTransferFundsRepository.findByItem(itemEntity);
                if (transferEntity != null) {
                    TransferFundsDetailsDTO details = new TransferFundsDetailsDTO();
                    details.setTransferType(transferEntity.getTransferType().name());
                    details.setTransferAmount(transferEntity.getTransferAmount());
                    details.setCommission(transferEntity.getCommission());
                    serviceDTO.setTransferFundsDetails(details);
                }
                break;

            case CODE_MORTGAGE:
                ItemMortgageEntity mortgageEntity = itemMortgageRepository.findByItem(itemEntity);
                if (mortgageEntity != null) {
                    MortgageDetailsDTO details = new MortgageDetailsDTO();
                    details.setLoanAmount(mortgageEntity.getLoanAmount());
                    details.setLoanTerm(mortgageEntity.getLoanTerm());
                    details.setInterestRate(mortgageEntity.getInterestRate());
                    serviceDTO.setMortgageDetails(details);
                }
                break;
        }
    }
}