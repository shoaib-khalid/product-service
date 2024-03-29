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
import com.kalsym.product.service.model.store.StoreDiscountProduct;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface StoreDiscountProductRepository
        extends PagingAndSortingRepository<StoreDiscountProduct, String>, JpaRepository<StoreDiscountProduct, String>, CustomRepository<StoreDiscountProduct, String> {
        
        List<StoreDiscountProduct> findByStoreDiscountId(@Param("storeDiscountId") String storeDiscountId);
        
        Optional<StoreDiscountProduct> findByStoreDiscountIdAndItemCode(@Param("storeDiscountId") String storeDiscountId, @Param("itemCode") String itemCode);
        
        Optional<StoreDiscountProduct> findByStoreDiscountIdAndCategoryId(@Param("storeDiscountId") String storeDiscountId, @Param("categoryId") String categoryId);
       
        <S extends Object> Page<S> findByStoreDiscountId(@Param("storeDiscountId") String storeDiscountId,  Pageable pgbl);
        
        List<StoreDiscountProduct> findByItemCode(@Param("itemCode") String itemCode);
}
