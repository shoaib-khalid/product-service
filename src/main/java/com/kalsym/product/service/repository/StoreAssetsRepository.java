package com.kalsym.product.service.repository;

import com.kalsym.product.service.enums.StoreAssetType;
import com.kalsym.product.service.model.store.StoreAssets;
import com.kalsym.product.service.model.product.ProductInventoryItem;
import com.kalsym.product.service.model.store.StoreWithDetails;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreAssetsRepository extends PagingAndSortingRepository<StoreAssets, String>, JpaRepository<StoreAssets, String> {

    List<StoreAssets> findByStoreId(@Param("storeId") String storeId);
    
    List<StoreAssets> findByStoreIdAndAssetType(@Param("storeId") String storeId, @Param("storeAssetType") StoreAssetType storeAssetType);
    
    @Transactional
    String deleteByStoreId(@Param("storeId") String storeId);
    
    
    @Query(
            " SELECT sa "
            + "FROM StoreAssets sa "
                    + " INNER JOIN store s ON sa.storeId=s.id "
            + "WHERE s.regionCountryId = :searchCountry AND sa.assetType = 'LogoUrl' "
            + "ORDER BY s.created DESC"
    )
    Page<StoreAssets> findByCountry(
            @Param("searchCountry") String searchCountry,
            Pageable pageable
    );
}
