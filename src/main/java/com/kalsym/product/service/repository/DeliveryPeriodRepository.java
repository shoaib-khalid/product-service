package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.DeliveryPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface DeliveryPeriodRepository extends PagingAndSortingRepository<DeliveryPeriod, String>, JpaRepository<DeliveryPeriod, String> {

}
