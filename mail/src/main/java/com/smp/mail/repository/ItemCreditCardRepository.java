package com.smp.mail.repository;

import com.smp.mail.entity.ItemCreditCardEntity;
import com.smp.mail.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCreditCardRepository extends JpaRepository<ItemCreditCardEntity, Long> {
    ItemCreditCardEntity findByItem(ItemEntity item);
}
