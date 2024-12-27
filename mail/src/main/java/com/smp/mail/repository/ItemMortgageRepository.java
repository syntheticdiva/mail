package com.smp.mail.repository;

import com.smp.mail.entity.ItemEntity;
import com.smp.mail.entity.ItemMortgageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMortgageRepository extends JpaRepository<ItemMortgageEntity, Long> {
    ItemMortgageEntity findByItem(ItemEntity item);
}
