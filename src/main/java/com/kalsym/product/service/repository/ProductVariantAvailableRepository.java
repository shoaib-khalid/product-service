package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductVariantAvailable;
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
public interface ProductVariantAvailableRepository extends PagingAndSortingRepository<ProductVariantAvailable, String>, JpaRepository<ProductVariantAvailable, String> {

    List<ProductVariantAvailable> findByProductId(String productId);
}
