package com.smp.mail.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "item")
@Data
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private ItemCreditCardEntity creditCardDetails;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private ItemTransferFundsEntity transferFundsDetails;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private ItemMortgageEntity mortgageDetails;
}
