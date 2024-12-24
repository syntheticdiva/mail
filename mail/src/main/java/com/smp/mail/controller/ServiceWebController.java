package com.smp.mail.controller;

import com.smp.mail.dto.ServiceOrderRequestDTO;
import com.smp.mail.service.ServiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping(ServiceWebController.SERVICE_BASE_PATH)
public class ServiceWebController {
    public static final String SERVICE_BASE_PATH = "/services";
    private static final String ORDER_ENDPOINT = "/order";
    private static final String SERVICES_VIEW = "services";
    private static final String SUCCESS_VIEW = "success";
    private static final String ATTR_SERVICES = "services";
    private static final String ATTR_SELECTION_DTO = "selectionDTO";

    private final ServiceOrderService serviceOrderService;

    @Autowired
    public ServiceWebController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @GetMapping
    public String showServicesPage(Model model) {
        var services = serviceOrderService.getAllServices();
        var selectionDTO = new ServiceOrderRequestDTO();

        model.addAttribute(ATTR_SERVICES, services);
        model.addAttribute(ATTR_SELECTION_DTO, selectionDTO);

        return SERVICES_VIEW;
    }

    @PostMapping(ORDER_ENDPOINT)
    public String orderServices(
            @ModelAttribute(ATTR_SELECTION_DTO) ServiceOrderRequestDTO orderRequest
    ) {
        serviceOrderService.processServiceOrder(orderRequest);

        return SUCCESS_VIEW;
    }
}