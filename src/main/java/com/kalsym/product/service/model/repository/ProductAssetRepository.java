package com.kalsym.product.service.model.repository;

import com.kalsym.product.service.model.product.ProductAsset;
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
public interface ProductAssetRepository extends PagingAndSortingRepository<ProductAsset, String>, JpaRepository<ProductAsset, String> {

    List<ProductAsset> findByProductId(@Param("productId") String productId);

}
