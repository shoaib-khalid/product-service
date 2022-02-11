package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductWithDetails;
import com.kalsym.product.service.model.store.StoreWithDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreWithDetailsRepository extends PagingAndSortingRepository<StoreWithDetails, String>, JpaRepository<StoreWithDetails, String>,  JpaSpecificationExecutor<StoreWithDetails> {

   Optional<StoreWithDetails> findByDomain(@Param("domain") String domain);
   
   Optional<StoreWithDetails> findByName(@Param("name") String name);
   

}
