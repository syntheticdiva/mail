package com.smp.mail.repository;

import com.smp.mail.entity.ItemEntity;
import com.smp.mail.entity.ItemTransferFundsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemTransferFundsRepository extends JpaRepository<ItemTransferFundsEntity, Long> {
    ItemTransferFundsEntity findByItem(ItemEntity item);
}
