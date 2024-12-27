package com.smp.mail.dto;

import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private Long id;
    private String code;
    private String name;
    private CreditCardDetailsDTO creditCardDetails;
    private TransferFundsDetailsDTO transferFundsDetails;
    private MortgageDetailsDTO mortgageDetails;
}