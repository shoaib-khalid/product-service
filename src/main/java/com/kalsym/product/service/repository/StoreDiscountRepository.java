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

import com.kalsym.product.service.model.store.StoreDiscount;
import com.kalsym.product.service.enums.StoreDiscountType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mohsin
 */
@Repository
public interface StoreDiscountRepository
        extends PagingAndSortingRepository<StoreDiscount, String>, JpaRepository<StoreDiscount, String>, JpaSpecificationExecutor<StoreDiscount> {
    

        List<StoreDiscount> findByStoreId(@Param("storeId") String storeId);
        
        @Query("SELECT m FROM StoreDiscount m WHERE m.storeId = :queryStoreId AND m.isActive=true AND m.startDate < :currentDate AND m.endDate > :currentDate") 
    List<StoreDiscount> findAvailableDiscount(
            @Param("queryStoreId") String storeId,
            @Param("currentDate") Date currentDate
            );
    
        @Query("SELECT m FROM StoreDiscount m WHERE m.isActive=true AND m.startDate < :currentDate AND m.endDate > :currentDate") 
       List<StoreDiscount> findAllAvailableDiscount(
             @Param("currentDate") Date currentDate
            );
    
        @Query("SELECT m FROM StoreDiscount m WHERE m.storeId = :queryStoreId "
                + "AND m.isActive=true AND m.discountType = :discountType "
                + "AND ("
                + "(:startDateTime >= m.startDate AND :startDateTime <= m.endDate) "
                + " OR (:endDateTime >= m.startDate AND :endDateTime <= m.endDate) "
                + " OR (:startDateTime <= m.startDate AND :endDateTime >= m.endDate) "
                + ")") 
    List<StoreDiscount> findAvailableDiscountDateRange(
            @Param("queryStoreId") String storeId,
            @Param("discountType") StoreDiscountType discountType,
            @Param("startDateTime") Date startDateTime,
            @Param("endDateTime") Date endDateTime
            );
    
    @Query("SELECT m FROM StoreDiscount m WHERE m.storeId = :queryStoreId "
                + "AND m.isActive=true AND m.id <> :discountId AND m.discountType = :discountType "
                + "AND ("
                + "(:startDateTime >= m.startDate AND :startDateTime <= m.endDate) "
                + " OR (:endDateTime >= m.startDate AND :endDateTime <= m.endDate) "
                + " OR (:startDateTime <= m.startDate AND :endDateTime >= m.endDate) "
                + ")") 
    List<StoreDiscount> findOtherAvailableDiscountDateRange(
            @Param("queryStoreId") String storeId,
            @Param("discountType") StoreDiscountType discountType,
            @Param("startDateTime") Date startDateTime,
            @Param("endDateTime") Date endDateTime,
            @Param("discountId") String discountId
            );
    
    
    
}
