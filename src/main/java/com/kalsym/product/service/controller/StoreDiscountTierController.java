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
import com.kalsym.product.service.repository.StoreDiscountTierRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.StoreDiscountRepository;

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
import com.kalsym.product.service.model.store.StoreDiscountTier;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author mohsin
 */
@RestController
@RequestMapping("/stores/{storeId}/discount/{discountId}/tier")
public class StoreDiscountTierController {

    @Autowired
    StoreDiscountTierRepository storeDiscountTierRepository;
    
    @Autowired
    StoreDiscountRepository storeDiscountRepository;
    
    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""})
    public ResponseEntity<HttpResponse> getDiscountTierByDiscountId(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String discountId) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Discount Id recieved: " + discountId);

        List<StoreDiscountTier> storeDiscountTierList = storeDiscountTierRepository.findByStoreDiscountId(discountId);

        if (storeDiscountTierList == null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountTier Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountTier Found");
        response.setData(storeDiscountTierList);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""})
    public ResponseEntity<HttpResponse> postStoreDiscountTier(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String discountId,
            @RequestBody StoreDiscountTier storeDiscountTier) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "discountId recieved: " + discountId);

        Optional<StoreDiscount> optStoreDiscount = storeDiscountRepository.findById(discountId);

        if (!optStoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
                
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Object:" + optStoreDiscount);
        storeDiscountTier.setStoreDiscountId(discountId);
        storeDiscountTierRepository.save(storeDiscountTier);
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {""})
    public ResponseEntity<HttpResponse> putStoreDiscountTier(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String discountId,
            @RequestBody StoreDiscountTier storeDiscountTier) {

       HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "discountId recieved: " + discountId);

        Optional<StoreDiscount> optStoreDiscount = storeDiscountRepository.findById(discountId);

        if (!optStoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Optional<StoreDiscountTier> optStoreDiscountTier = storeDiscountTierRepository.findById(storeDiscountTier.getId());

        if (!optStoreDiscountTier.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountTier Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
                
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountTier Object:" + optStoreDiscountTier);
        storeDiscountTier.setStoreDiscountId(discountId);
        storeDiscountTierRepository.save(storeDiscountTier);
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
