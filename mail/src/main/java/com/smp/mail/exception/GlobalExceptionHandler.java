package com.smp.mail.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> handleEmailSendingException(EmailSendingException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Ошибка при отправке электронной почты");
        errorResponse.setResolution("Проверьте корректность email и повторите попытку");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleServiceNotFoundException(ServiceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Услуга не найдена");
        errorResponse.setResolution("Проверьте корректность выбранных услуг");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
    @ExceptionHandler(ServiceRetrievalException.class)
    public ResponseEntity<ErrorResponse> handleServiceRetrievalException(ServiceRetrievalException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Ошибка получения списка услуг");
        errorResponse.setResolution("Повторите попытку или обратитесь в службу поддержки");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
    @ExceptionHandler(InvalidServiceSelectionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidServiceSelectionException(InvalidServiceSelectionException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Некорректный выбор услуг");
        errorResponse.setResolution("Проверьте выбранные услуги и повторите заказ");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Непредвиденная ошибка в системе");
        errorResponse.setResolution("Обратитесь в службу поддержки");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        log.error("Illegal argument error: ", ex);

        model.addAttribute("error", ex.getMessage());
        model.addAttribute("cause", "Некорректные входные данные");
        model.addAttribute("resolution", "Проверьте правильность заполнения формы");

        return "error";
    }
    @ExceptionHandler(ServiceDatabaseConnectionException.class)
    public ResponseEntity<ErrorResponse> handleServiceDatabaseConnectionException(ServiceDatabaseConnectionException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Ошибка подключения к базе данных");
        errorResponse.setResolution("Повторите попытку позже или обратитесь в службу поддержки");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }
    @ExceptionHandler(ServiceRepositoryException.class)
    public ResponseEntity<ErrorResponse> handleServiceRepositoryException(ServiceRepositoryException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Ошибка при работе с репозиторием услуг");
        errorResponse.setResolution("Повторите операцию или обратитесь в службу поддержки");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
    @ExceptionHandler({
            NoHandlerFoundException.class,
            NoResourceFoundException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public String handleNotFoundExceptions(Exception ex, Model model) {
        log.error("URL not found error: ", ex);

        String resourcePath = "Неизвестный адрес";
        if (ex instanceof NoResourceFoundException resourceEx) {
            resourcePath = resourceEx.getResourcePath();
        } else if (ex instanceof NoHandlerFoundException handlerEx) {
            resourcePath = handlerEx.getRequestURL();
        }

        model.addAttribute("error", "Страница не найдена");
        model.addAttribute("cause", "Указанный адрес не существует: " + resourcePath);
        model.addAttribute("resolution", "Проверьте правильность введенного URL");

        return "error";
    }

}

