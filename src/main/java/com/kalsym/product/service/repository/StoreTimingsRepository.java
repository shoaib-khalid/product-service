package com.kalsym.product.service.repository;

import com.google.common.base.Optional;
import com.kalsym.product.service.model.store.StoreTiming;
import com.kalsym.product.service.model.store.StoreTimingIdentity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreTimingsRepository extends PagingAndSortingRepository<StoreTiming, StoreTimingIdentity>, JpaRepository<StoreTiming, StoreTimingIdentity> {

    List<StoreTiming> findByStoreId(@Param("storeId") String storeId);
    
    // @Transactional   //javax.persistence.TransactionRequiredException: Executing an update/delete query
    // @Modifying      //To handle error Not supported for DML operations
    // @Query(
    //     " DELETE "
    //     + "FROM StoreTiming st "
    //     + "WHERE st.storeId = :storeId"
    // )
    // void  deleteByStoreId(
    //         @Param("storeId") String storeId
    // );

    Optional<StoreTiming> findByStoreIdAndDay(@Param("storeId") String storeId, @Param("day") String day);

}
