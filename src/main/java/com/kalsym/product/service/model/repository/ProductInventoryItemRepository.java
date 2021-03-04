package com.kalsym.product.service.model.repository;

import com.kalsym.product.service.model.ProductInventoryItem;
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
public interface ProductInventoryItemRepository extends PagingAndSortingRepository<ProductInventoryItem, String>, JpaRepository<ProductInventoryItem, String> {

    //List<ProductInventoryItem> findByItemCode(@Param("itemCode") String itemCode);
}
