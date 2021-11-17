package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductPackageOptionDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 7cu
 */
@Repository
public interface ProductPackageOptionDetailRepository extends PagingAndSortingRepository<ProductPackageOptionDetail, String>, JpaRepository<ProductPackageOptionDetail, String> {
    
    @Transactional
    String deleteByProductPackageOptionId(@Param("productPackageOptionId") String productPackageOptionId);
}
