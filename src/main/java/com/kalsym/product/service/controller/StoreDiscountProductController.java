/*
 * Copyright (C) 2021 mohsi
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
package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.repository.StoreDiscountProductRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.StoreDiscountRepository;
import com.kalsym.product.service.repository.ProductInventoryRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.StoreCategoryRepository;
import com.kalsym.product.service.enums.StoreDiscountType;

import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import com.kalsym.product.service.model.store.StoreDiscount;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.model.store.StoreDiscountProduct;
import com.kalsym.product.service.model.ItemDiscount;
import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.product.Product;

import java.util.ArrayList;
import java.util.Collections;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author mohsin
 */
@RestController
@RequestMapping("/stores/{storeId}/discount/{discountId}/product")
public class StoreDiscountProductController {

    @Autowired
    StoreDiscountProductRepository storeDiscountProductRepository;

    @Autowired
    StoreDiscountRepository storeDiscountRepository;

    @Autowired
    StoreRepository storeRepository;
    
    @Autowired
    ProductInventoryRepository productInventoryRepository;
    
    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @GetMapping(path = {""})
    public ResponseEntity<HttpResponse> getDiscountProductByDiscountId(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String discountId) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Discount Id recieved: " + discountId);

        List<StoreDiscountProduct> storeDiscountProductList = storeDiscountProductRepository.findByStoreDiscountId(discountId);
        if (storeDiscountProductList == null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountProduct Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountProduct Found");
        response.setData(storeDiscountProductList);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<HttpResponse> getDiscountProductById(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String discountId,
            @PathVariable(required = true) String id
    ) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Discount Product Id recieved: " + id);

        Optional<StoreDiscountProduct> storeDiscountProduct = storeDiscountProductRepository.findById(id);

        if (!storeDiscountProduct.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountProduct Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountProduct Found");
        response.setData(storeDiscountProduct.get());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }    

    @PostMapping(path = {""})
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
    public ResponseEntity<HttpResponse> postStoreDiscountProduct(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String discountId,
            @RequestBody StoreDiscountProduct storeDiscountProduct) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "discountId received: " + discountId);

        Optional<StoreDiscount> optStoreDiscount = storeDiscountRepository.findById(discountId);

        if (!optStoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }        
        
        if (optStoreDiscount.get().getDiscountType().equals(StoreDiscountType.ITEM)) {
            if (storeDiscountProduct.getItemCode()!=null) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "validate discountId: " + storeDiscountProduct.getStoreDiscountId()+" itemCode:"+storeDiscountProduct.getItemCode());
                
                //check if item exist 
                Optional<ProductInventory> optItem = productInventoryRepository.findById(storeDiscountProduct.getItemCode());
                if (!optItem.isPresent()) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "ItemCode Not Found");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    response.setError("ItemCode not found");
                    return ResponseEntity.status(response.getStatus()).body(response);
                } 
                
                //check if item exist in store
                Optional<Product> optProduct = productRepository.findById(optItem.get().getProductId());
                if (!optProduct.isPresent()) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "ItemCode Product Not Found");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    response.setError("Product not found");
                    return ResponseEntity.status(response.getStatus()).body(response);
                } 
                
                if (!optProduct.get().getStoreId().equals(storeId)) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Item code not belong to this storeId");
                    response.setStatus(HttpStatus.CONFLICT);
                    response.setError("ItemCode not authorized");
                    return ResponseEntity.status(response.getStatus()).body(response);
                }
                
                //check if item code already exist
                Optional<StoreDiscountProduct> optdiscountItem = storeDiscountProductRepository.findByStoreDiscountIdAndItemCode(storeDiscountProduct.getStoreDiscountId(), storeDiscountProduct.getItemCode());
                if (optdiscountItem.isPresent()) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Not Found");
                    response.setStatus(HttpStatus.CONFLICT);
                    response.setError("Item already exist");
                    return ResponseEntity.status(response.getStatus()).body(response);
                } 
            } else if (storeDiscountProduct.getCategoryId()!=null) {        
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "validate categoryId: " + storeDiscountProduct.getCategoryId());
                
               
                //check if item exist in store
                Optional<StoreCategory> optCategory = storeCategoryRepository.findById(storeDiscountProduct.getCategoryId());
                if (!optCategory.isPresent()) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "CategoryId Not Found");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    response.setError("Category not found");
                    return ResponseEntity.status(response.getStatus()).body(response);
                } 
                
                if (!optCategory.get().getStoreId().equals(storeId)) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "CategoryId not belong to this storeId");
                    response.setStatus(HttpStatus.CONFLICT);
                    response.setError("Category not authorized");
                    return ResponseEntity.status(response.getStatus()).body(response);
                }
                
                //check if item code already exist
                Optional<StoreDiscountProduct> optdiscountItem = storeDiscountProductRepository.findByCategoryId(storeDiscountProduct.getCategoryId());
                if (optdiscountItem.isPresent()) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Not Found");
                    response.setStatus(HttpStatus.CONFLICT);
                    response.setError("Category already exist");
                    return ResponseEntity.status(response.getStatus()).body(response);
                } 
            }
        }
        
        storeDiscountProduct = storeDiscountProductRepository.save(storeDiscountProduct);
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "storeDiscountProduct Object:" + storeDiscountProduct);
        response.setData(storeDiscountProduct);
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {""})
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
    public ResponseEntity<HttpResponse> putStoreDiscountProduct(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String discountId,
            @RequestBody StoreDiscountProduct storeDiscountProduct) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "discountId recieved: " + discountId);

        Optional<StoreDiscount> optStoreDiscount = storeDiscountRepository.findById(discountId);

        if (!optStoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<StoreDiscountProduct> optStoreDiscountProduct = storeDiscountProductRepository.findById(storeDiscountProduct.getId());

        if (!optStoreDiscountProduct.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountProduct Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountProduct Object:" + optStoreDiscountProduct);
        storeDiscountProduct = storeDiscountProductRepository.save(storeDiscountProduct);
        response.setData(storeDiscountProduct);
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
    @DeleteMapping(path = {"/{id}"}, name = "store-discounts-tier-delete-by-id", produces = "application/json")
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
    public ResponseEntity<HttpResponse> deleteStoreDiscountProductById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String discountId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "discountId: " + storeId);

        Optional<StoreDiscount> optStoreDiscount = storeDiscountRepository.findById(discountId);

        if (!optStoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND discountId: " + discountId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("StoreDiscount not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND discountId: " + discountId);

        Optional<StoreDiscountProduct> optStoreDiscountProduct = storeDiscountProductRepository.findById(id);

        if (!optStoreDiscountProduct.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "storeDiscountProduct NOT_FOUND storeId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("Discount Product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "discountTIer FOUND tierId: " + id);

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "discountTIer found for id: {}", id);

        StoreDiscountProduct p = optStoreDiscountProduct.get();
        storeDiscountProductRepository.deleteById(p.getId());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
