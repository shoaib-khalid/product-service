package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductInventoryItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface ProductInventoryItemRepository extends PagingAndSortingRepository<ProductInventoryItem, String>, JpaRepository<ProductInventoryItem, String> {

    List<ProductInventoryItem> findByProductId(@Param("productId") String productId);
    
    Optional<ProductInventoryItem> findByItemCodeAndProductVariantAvailableId(@Param("itemCode") String itemCode, @Param("itemCode") String productVariantAvailableId);
}
