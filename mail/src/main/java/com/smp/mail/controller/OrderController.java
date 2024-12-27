package com.smp.mail.controller;

import com.smp.mail.dto.OrderDTO;
import com.smp.mail.entity.OrderEntity;
import com.smp.mail.entity.ServiceEntity;
import com.smp.mail.service.OrderProcessingService;
import com.smp.mail.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/orders")
public class OrderController {
    private static final String PATH_CREATE = "/create";
    private static final String PATH_SAVE = "/save";
    private static final String PATH_CONFIRMATION = "/confirmation/{orderId}";
    private static final String VIEW_ORDER_CREATION = "order-creation";
    private static final String VIEW_ORDER_CONFIRMATION = "order-confirmation";

    private static final String ATTR_SERVICES = "services";
    private static final String ATTR_ORDER = "order";

    @Autowired
    private OrderProcessingService orderProcessingService;

    @Autowired
    private OrderService orderService;

    @GetMapping(PATH_CREATE)
    public String showOrderCreationPage(Model model) {
        List<ServiceEntity> services = orderProcessingService.getAllServices();
        model.addAttribute(ATTR_SERVICES, services);
        return VIEW_ORDER_CREATION;
    }

    @PostMapping(PATH_SAVE)
    @ResponseBody
    public ResponseEntity<?> saveOrder(@RequestBody OrderDTO orderDTO) {
        Map<String, Object> response = orderProcessingService.saveOrder(orderDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping(PATH_CONFIRMATION)
    public String orderConfirmation(@PathVariable Long orderId, Model model) {
        OrderEntity orderEntity = orderService.findById(orderId);
        model.addAttribute(ATTR_ORDER, orderEntity);
        return VIEW_ORDER_CONFIRMATION;
    }
}