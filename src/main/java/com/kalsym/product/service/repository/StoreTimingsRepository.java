package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.StoreTiming;
import com.kalsym.product.service.model.store.StoreTimingIdentity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreTimingsRepository extends PagingAndSortingRepository<StoreTiming, StoreTimingIdentity>, JpaRepository<StoreTiming, StoreTimingIdentity> {

    List<StoreTiming> findByStoreId(@Param("storeId") String storeId);

}
