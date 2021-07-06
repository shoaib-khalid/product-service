package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.product.ProductInventoryWithProduct;
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
public interface ProductInventoryWithProductRepository extends PagingAndSortingRepository<ProductInventoryWithProduct, String>, JpaRepository<ProductInventoryWithProduct, String> {
    
    ProductInventoryWithProduct findByItemCode(@Param("itemCode") String itemCode);
    
}
