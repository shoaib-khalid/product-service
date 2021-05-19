package com.kalsym.product.service.model.repository;

import com.kalsym.product.service.model.StoreWithDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreWithDetailsRepository extends PagingAndSortingRepository<StoreWithDetails, String>, JpaRepository<StoreWithDetails, String> {

   

}
