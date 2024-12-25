package com.smp.mail.repository;

import com.smp.mail.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // Можно добавить дополнительные методы при необходимости
}