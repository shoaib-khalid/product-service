package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.kalsym.product.service.model.store.StoreDeliveryPeriod;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreDeliveryPeriodsRepository extends PagingAndSortingRepository<StoreDeliveryPeriod, String>, JpaRepository<StoreDeliveryPeriod, String> {

    List<StoreDeliveryPeriod> findByStoreId(@Param("storeId") String storeId);
    
    
    @Transactional 
    @Modifying
    @Query("UPDATE StoreDeliveryPeriod m SET m.enabled = :searchDeliveryEnabled WHERE m.storeId = :searchStoreId AND m.deliveryPeriod = :searchDeliveryOption") 
    void UpdateStoreDeliveryOption(
            @Param("searchStoreId") String searchStoreId,
            @Param("searchDeliveryOption") String searchDeliveryOption,
            @Param("searchDeliveryEnabled") Boolean searchDeliveryEnabled
            );
    
    @Transactional
    String deleteByStoreId(@Param("storeId") String storeId);
}
