package com.kalsym.product.service.repository;

import com.kalsym.product.service.enums.StoreAssetType;
import com.kalsym.product.service.model.store.StoreAssets;
import com.kalsym.product.service.model.product.ProductInventoryItem;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreAssetsRepository extends PagingAndSortingRepository<StoreAssets, String>, JpaRepository<StoreAssets, String> {

    List<StoreAssets> findByStoreId(@Param("storeId") String storeId);
    
    List<StoreAssets> findByStoreIdAndAssetType(@Param("storeId") String storeId, @Param("storeAssetType") StoreAssetType storeAssetType);
}
