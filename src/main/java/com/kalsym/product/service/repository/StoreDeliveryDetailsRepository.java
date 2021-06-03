package com.kalsym.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.kalsym.product.service.model.store.StoreDeliveryDetail;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreDeliveryDetailsRepository extends PagingAndSortingRepository<StoreDeliveryDetail, String>, JpaRepository<StoreDeliveryDetail, String> {

}
