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
import com.kalsym.product.service.repository.StoreDiscountRepository;
import com.kalsym.product.service.repository.StoreRepository;

import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import java.util.List;
import java.util.Date;
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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author mohsin
 */
@RestController
@RequestMapping("/stores/{storeId}/discount")
public class StoreDiscountController {

    @Autowired
    StoreDiscountRepository storeDiscountRepository;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""})
    public ResponseEntity<HttpResponse> getDiscountByStoreId(HttpServletRequest request,
            @PathVariable(required = true) String storeId) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        List<StoreDiscount> storeDiscountList = storeDiscountRepository.findByStoreId(storeId);

        if (storeDiscountList == null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Found");
        response.setData(storeDiscountList);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/active"})
    public ResponseEntity<HttpResponse> getActiveDiscountByStoreId(HttpServletRequest request,
            @PathVariable(required = true) String storeId) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        List<StoreDiscount> storeDiscountList = storeDiscountRepository.findAvailableDiscount(storeId, new Date());

        if (storeDiscountList == null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Found");
        response.setData(storeDiscountList);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""})
    public ResponseEntity<HttpResponse> postStoreDiscount(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @RequestBody StoreDiscount storeDiscount) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
                
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Object:" + optStore);
        storeDiscount.setStoreId(storeId);
        storeDiscountRepository.save(storeDiscount);
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {""})
    public ResponseEntity<HttpResponse> putStoreDiscount(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @RequestBody StoreDiscount storeDiscount) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Optional<StoreDiscount> optStoreDiscount = storeDiscountRepository.findById(storeDiscount.getId());

        if (!optStoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Found Object:" + optStoreDiscount);
        storeDiscount.setStoreId(storeId);
        storeDiscountRepository.save(storeDiscount);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
