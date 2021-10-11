/*
 * Copyright (C) 2021 taufik
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
package com.kalsym.product.service.filter;

import com.kalsym.product.service.ProductServiceApplication;

import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.store.StoreCategory;

import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.StoreCategoryRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.utility.Logger;
import com.kalsym.product.service.utility.Validation;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author taufik
 */
@Component(value="customOwnerVerifier")
public class CustomOwnerVerifier {
 
   @Autowired
   StoreRepository storeRepository;
   
   @Autowired
   ProductRepository productRepository;
   
   @Autowired
   StoreCategoryRepository storeCategoryRepository;
   
   private final String logPrefix = "CustomOwnerVerifier";
   
   public boolean VerifyProduct(String productId) {
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "VerifyProduct for productId:"+productId, "");
        Optional<Product> optProduct = productRepository.findById(productId);
        if (!optProduct.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "Product Not found", "");
            return false;
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "Product found. StoreId:"+optProduct.get().getStoreId(), "");
         if (!Validation.VerifyStoreId(optProduct.get().getStoreId(), storeRepository)) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "Unathourized productId", "");
            return false;
        }
       
        return true;
    }
   
    public boolean VerifyStore(String storeId) {
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "VerifyStore for storeId:"+storeId, "");
        if (!Validation.VerifyStoreId(storeId, storeRepository)) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "Unathourized storeId", "");
            return false;
        }
       
        return true;
    }
    
     public boolean VerifyStoreCategory(String storeCategoryId) {
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "VerifyStoreCategory for storeCategoryId:"+storeCategoryId, "");
        Optional<StoreCategory> storeCategory = storeCategoryRepository.findById(storeCategoryId);
        if (!storeCategory.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "StoreCategory Not found", "");
            return false;
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "Product found. StoreId:"+storeCategory.get().getStoreId(), "");
         if (!Validation.VerifyStoreId(storeCategory.get().getStoreId(), storeRepository)) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logPrefix, "Unathourized productId", "");
            return false;
        }
       
        return true;
    }
   
}
