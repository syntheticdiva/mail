package com.smp.mail.controller;

import com.smp.mail.service.ErrorHandlingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    private static final String ERROR_PATH = "/error";
    private static final String ERROR_VIEW = "error";

    private final ErrorHandlingService errorHandlingService;

    @Autowired
    public CustomErrorController(ErrorHandlingService errorHandlingService) {
        this.errorHandlingService = errorHandlingService;
    }

    @RequestMapping(ERROR_PATH)
    public String handleError(HttpServletRequest request, Model model) {
        errorHandlingService.handleError(request, model);
        return ERROR_VIEW;
    }
}