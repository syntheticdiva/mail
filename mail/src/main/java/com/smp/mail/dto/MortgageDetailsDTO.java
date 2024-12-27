package com.smp.mail.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class MortgageDetailsDTO {
    private BigDecimal loanAmount;
    private Integer loanTerm;
    private BigDecimal interestRate;
}
