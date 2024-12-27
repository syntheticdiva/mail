package com.smp.mail.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferFundsDetailsDTO {
    private String transferType;
    private BigDecimal transferAmount;
    private BigDecimal commission;
}
