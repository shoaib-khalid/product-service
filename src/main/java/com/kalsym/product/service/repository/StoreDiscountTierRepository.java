/*
 * Copyright (C) 2021 mohsin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductAsset;
import com.kalsym.product.service.model.store.StoreAsset;
import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.model.store.StoreDiscount;
import com.kalsym.product.service.model.store.StoreDiscountTier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mohsin
 */
@Repository
public interface StoreDiscountTierRepository
        extends PagingAndSortingRepository<StoreDiscountTier, String>, JpaRepository<StoreDiscountTier, String> {
        
        List<StoreDiscountTier> findByStoreDiscountId(@Param("storeDiscountId") String storeDiscountId);

        @Query("SELECT m FROM StoreDiscountTier m WHERE m.storeDiscountId = :discountId "
                + "AND ("
                + "(:startSalesAmount >= m.startTotalSalesAmount AND :startSalesAmount <= m.endTotalSalesAmount )"
                + " OR (:endSalesAmount >= m.startTotalSalesAmount AND :endSalesAmount <= m.endTotalSalesAmount )"
                + " OR (:startSalesAmount <= m.startTotalSalesAmount AND :endSalesAmount >= m.endTotalSalesAmount)"
                + ")") 
        List<StoreDiscountTier> findDiscountTierAmountRange(
            @Param("discountId") String discountId,
            @Param("startSalesAmount") Double startSalesAmount,
            @Param("endSalesAmount") Double endSalesAmount
            );
        
        @Query("SELECT m FROM StoreDiscountTier m WHERE m.storeDiscountId = :discountId "
                + "AND m.id <> :tierId AND ("
                + "(:startSalesAmount >= m.startTotalSalesAmount AND :startSalesAmount <= m.endTotalSalesAmount )"
                + " OR (:endSalesAmount >= m.startTotalSalesAmount AND :endSalesAmount <= m.endTotalSalesAmount )"
                + " OR (:startSalesAmount <= m.startTotalSalesAmount AND :endSalesAmount >= m.endTotalSalesAmount)"
                + ")") 
        List<StoreDiscountTier> findOtherDiscountTierAmountRange(
            @Param("discountId") String discountId,
            @Param("startSalesAmount") Double startSalesAmount,
            @Param("endSalesAmount") Double endSalesAmount,
            @Param("tierId") String tierId
            );
        
        
        @Query("SELECT m FROM StoreDiscountTier m WHERE m.storeDiscountId = :discountId "
                + "AND m.startTotalSalesAmount = :startSalesAmount") 
        List<StoreDiscountTier> findDiscountTierStartAmount(
            @Param("discountId") String discountId,
            @Param("startSalesAmount") Double startSalesAmount
           );
        
        @Query("SELECT m FROM StoreDiscountTier m WHERE m.storeDiscountId = :discountId "
                + "AND m.startTotalSalesAmount = :startSalesAmount AND m.id <> :tierId") 
        List<StoreDiscountTier> findOtherDiscountTierStartAmount(
            @Param("discountId") String discountId,
            @Param("startSalesAmount") Double startSalesAmount,
            @Param("tierId") String tierId
           );
}
