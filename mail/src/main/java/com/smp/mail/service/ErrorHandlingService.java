package com.smp.mail.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ErrorHandlingService {
    private static final String DEFAULT_ERROR = "Произошла ошибка";
    private static final String DEFAULT_CAUSE = "Неизвестная ошибка";
    private static final String DEFAULT_RESOLUTION = "Попробуйте еще раз";

    public void handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            switch (statusCode) {
                case 404 -> {
                    model.addAttribute("error", "Страница не найдена");
                    model.addAttribute("cause", "Указанный адрес не существует");
                    model.addAttribute("resolution", "Проверьте правильность введенного URL");
                }
                case 500 -> {
                    model.addAttribute("error", "Внутренняя ошибка сервера");
                    model.addAttribute("cause", "Произошла непредвиденная ошибка");
                    model.addAttribute("resolution", "Попробуйте позже или обратитесь в поддержку");
                }
                default -> {
                    model.addAttribute("error", DEFAULT_ERROR);
                    model.addAttribute("cause", DEFAULT_CAUSE);
                    model.addAttribute("resolution", DEFAULT_RESOLUTION);
                }
            }
        } else {
            model.addAttribute("error", DEFAULT_ERROR);
            model.addAttribute("cause", DEFAULT_CAUSE);
            model.addAttribute("resolution", DEFAULT_RESOLUTION);
        }
    }
}
