package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.StoreWithDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreWithDetailsRepository extends PagingAndSortingRepository<StoreWithDetails, String>, JpaRepository<StoreWithDetails, String> {

   Optional<StoreWithDetails> findByDomain(@Param("domain") String domain);
   
   Optional<StoreWithDetails> findByName(@Param("name") String name);

}
