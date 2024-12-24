package com.smp.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ServiceOrderRequestDTO {
    @Email(message = "Некорректный email")
    @NotEmpty(message = "Email не может быть пустым")
    private String userEmail;

    @NotEmpty(message = "Выберите хотя бы одну услугу")
    private List<Long> selectedServiceIds = new ArrayList<>();
}