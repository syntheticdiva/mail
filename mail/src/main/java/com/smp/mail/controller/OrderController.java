package com.smp.mail.controller;

import com.smp.mail.dto.OrderDTO;
import com.smp.mail.dto.ServiceDTO;
import com.smp.mail.entity.OrderEntity;
import com.smp.mail.entity.ServiceEntity;
import com.smp.mail.repository.ServiceRepository;
import com.smp.mail.service.EmailService;
import com.smp.mail.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired private ServiceRepository serviceRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/create")
    public String showOrderCreationPage(Model model) {
        List<ServiceEntity> services = serviceRepository.findAll();
        model.addAttribute("services", services);
        return "order-creation";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveOrder(@RequestBody OrderDTO orderDTO) {
        try {
            OrderEntity savedOrder = orderService.createOrder(orderDTO);

            List<ServiceDTO> serviceDTOs = savedOrder.getItemEntities().stream()
                    .map(item -> {
                        ServiceDTO dto = new ServiceDTO();
                        dto.setId(item.getService().getId());
                        dto.setName(item.getService().getName());
                        dto.setCode(item.getService().getCode());
                        return dto;
                    })
                    .collect(Collectors.toList());

            emailService.sendOrderConfirmation(
                    savedOrder.getUserEmail(),
                    serviceDTOs
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedOrder.getId());
            response.put("email", savedOrder.getUserEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Ошибка сохранения заказа: " + e.getMessage());
        }
    }

    @GetMapping("/confirmation/{orderId}")
    public String orderConfirmation(@PathVariable Long orderId, Model model) {
        OrderEntity orderEntity = orderService.findById(orderId);
        model.addAttribute("order", orderEntity);
        return "order-confirmation";
    }
}