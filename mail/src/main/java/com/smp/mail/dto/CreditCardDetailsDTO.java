package com.smp.mail.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditCardDetailsDTO {
    private BigDecimal creditLimit;
    private BigDecimal interestRate;
    private Integer loanTerm;
}