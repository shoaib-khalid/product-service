package com.kalsym.product.service.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.product.ProductInventoryItemMain;

@Repository
public interface ProductInventoryItemMainRepository extends JpaRepository<ProductInventoryItemMain, String> {
    

    List<ProductInventoryItemMain> findByProductId(@Param("productId") String productId);

}
