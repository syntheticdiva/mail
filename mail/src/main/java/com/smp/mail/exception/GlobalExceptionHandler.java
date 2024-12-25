package com.smp.mail.exception;

import jakarta.persistence.EntityNotFoundException;
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
    @ExceptionHandler(ConfigFileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConfigFileNotFoundException(ConfigFileNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Ошибка конфигурации");
        errorResponse.setCause("Отсутствует файл config.txt");
        errorResponse.setResolution("Создайте файл config.txt в корневой директории приложения");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
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
    @ExceptionHandler(InvalidOrderDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderDataException(InvalidOrderDataException ex) {
        log.error("Ошибка валидации данных заказа", ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Некорректные данные заказа");
        errorResponse.setResolution("Проверьте правильность введенных данных");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    @ExceptionHandler(OrderItemValidationException.class)
    public ResponseEntity<ErrorResponse> handleOrderItemValidationException(OrderItemValidationException ex) {
        log.error("Ошибка валидации услуг в заказе", ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Ошибка в выбранных услугах");
        errorResponse.setResolution("Проверьте корректность выбранных услуг");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(OrderCreationException.class)
    public ResponseEntity<ErrorResponse> handleOrderCreationException(OrderCreationException ex) {
        log.error("Ошибка создания заказа", ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Не удалось создать заказ");
        errorResponse.setResolution("Попробуйте еще раз или обратитесь в поддержку");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(OrderPersistenceException.class)
    public ResponseEntity<ErrorResponse> handleOrderPersistenceException(OrderPersistenceException ex) {
        log.error("Ошибка сохранения заказа", ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Ошибка сохранения заказа");
        errorResponse.setResolution("Попробуйте позже или обратитесь в поддержку");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Сущность не найдена", ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCause("Запрошенная сущность не найдена");
        errorResponse.setResolution("Проверьте корректность идентификатора");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
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

