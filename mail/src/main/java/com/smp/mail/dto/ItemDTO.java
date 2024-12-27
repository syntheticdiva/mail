package com.smp.mail.dto;

import lombok.Data;

@Data
public class ItemDTO {
    private Long serviceId;
    private CreditCardDetailsDTO creditCardDetails;
    private TransferFundsDetailsDTO transferFundsDetails;
    private MortgageDetailsDTO mortgageDetails;
}
