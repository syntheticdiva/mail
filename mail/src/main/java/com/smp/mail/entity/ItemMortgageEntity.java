package com.smp.mail.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "item_mortgage")
@Data
public class ItemMortgageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private ItemEntity item;

    @Column(name = "loan_amount", precision = 19, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "loan_term")
    private Integer loanTerm;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;
}
