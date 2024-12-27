package com.smp.mail.entity;

import com.smp.mail.enums.TransferType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "item_transfer_funds")
@Data
public class ItemTransferFundsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private ItemEntity item;

    @Column(name = "transfer_type")
    @Enumerated(EnumType.STRING)
    private TransferType transferType;

    @Column(name = "transfer_amount", precision = 19, scale = 2)
    private BigDecimal transferAmount;

    @Column(name = "commission", precision = 19, scale = 2)
    private BigDecimal commission;

}
