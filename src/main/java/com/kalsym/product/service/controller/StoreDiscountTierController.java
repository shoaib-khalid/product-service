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
        Collections.sort(storeDiscountTierList);
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
    
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<HttpResponse> getDiscountTierById(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String discountId,
            @PathVariable(required = true) String id
    ) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Discount Tier Id recieved: " + id);

        Optional<StoreDiscountTier> storeDiscountTier = storeDiscountTierRepository.findById(id);

        if (!storeDiscountTier.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountTier Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscountTier Found");
        response.setData(storeDiscountTier.get());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }    

    @PostMapping(path = {""})
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
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
        
        List<StoreDiscountTier> allDiscountTier = null;
        if (storeDiscountTier.getEndTotalSalesAmount()==null) {
           //check for same start amount
            List<StoreDiscountTier> existingSameTier = storeDiscountTierRepository.findDiscountTierStartAmount(discountId, storeDiscountTier.getStartTotalSalesAmount());
            if (existingSameTier.size()>0) {
                 //already exist
                 List<String> errors = new ArrayList<>();
                 String errorMsg = "Overlap discount tier with "+existingSameTier.get(0).getStartTotalSalesAmount()+" - "+existingSameTier.get(0).getEndTotalSalesAmount();
                 errors.add(errorMsg);
                 response.setData(errors);
                 Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Overlap tier Id:"+existingSameTier.get(0).getId()+" StartAmount:"+existingSameTier.get(0).getStartTotalSalesAmount()+" EndAmount:"+existingSameTier.get(0).getEndTotalSalesAmount());
                 response.setMessage(errorMsg);
                 return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }
            allDiscountTier = storeDiscountTierRepository.findByStoreDiscountId(discountId);                        
            //add into existing record and sort
            allDiscountTier.add(storeDiscountTier);
            Collections.sort(allDiscountTier);
            
            //save again all tier after sort
            for (int i=0;i<allDiscountTier.size();i++) {
                StoreDiscountTier currentTier = allDiscountTier.get(i);
                currentTier.setEndTotalSalesAmount(9999999.00);
                storeDiscountTierRepository.save(currentTier);
                if (i>0) {
                    StoreDiscountTier prevtier = allDiscountTier.get(i-1);
                    Double endAmount = currentTier.getStartTotalSalesAmount()-0.01;
                    prevtier.setEndTotalSalesAmount(endAmount);
                    storeDiscountTierRepository.save(prevtier);
                }
            }
        } else {        
            //check for overlap
            List<StoreDiscountTier> existingDiscountTierList = storeDiscountTierRepository.findDiscountTierAmountRange(discountId, storeDiscountTier.getStartTotalSalesAmount(), storeDiscountTier.getEndTotalSalesAmount());
            if (existingDiscountTierList.size()>0) {
                StoreDiscountTier activeDiscountTier = existingDiscountTierList.get(0);
                List<String> errors = new ArrayList<>();
                String errorMsg = "Overlap discount tier with "+activeDiscountTier.getStartTotalSalesAmount()+" - "+activeDiscountTier.getEndTotalSalesAmount();
                errors.add(errorMsg);
                response.setData(errors);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Overlap tier Id:"+activeDiscountTier.getId()+" StartAmount:"+activeDiscountTier.getStartTotalSalesAmount()+" EndAmount:"+activeDiscountTier.getEndTotalSalesAmount());
                response.setMessage(errorMsg);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }
            //save tier
            response.setData(storeDiscountTierRepository.save(storeDiscountTier));
        }
        
        allDiscountTier = storeDiscountTierRepository.findByStoreDiscountId(discountId);
        Collections.sort(allDiscountTier);
        response.setData(allDiscountTier);
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {""})
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
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
        
        List<StoreDiscountTier> allDiscountTier = null;
        if (storeDiscountTier.getEndTotalSalesAmount()==null) {
           //check for same start amount
            List<StoreDiscountTier> existingSameTier = storeDiscountTierRepository.findOtherDiscountTierStartAmount(discountId, storeDiscountTier.getStartTotalSalesAmount(), storeDiscountTier.getId());
            if (existingSameTier.size()>0) {
                 //already exist
                 List<String> errors = new ArrayList<>();
                 String errorMsg = "Overlap discount tier with "+existingSameTier.get(0).getStartTotalSalesAmount()+" - "+existingSameTier.get(0).getEndTotalSalesAmount();
                 errors.add(errorMsg);
                 response.setData(errors);
                 Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Overlap tier Id:"+existingSameTier.get(0).getId()+" StartAmount:"+existingSameTier.get(0).getStartTotalSalesAmount()+" EndAmount:"+existingSameTier.get(0).getEndTotalSalesAmount());
                 response.setMessage(errorMsg);
                 return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }            
            //save discount
            storeDiscountTierRepository.save(storeDiscountTier);
            //sort all tier
            allDiscountTier = storeDiscountTierRepository.findByStoreDiscountId(discountId);                        
            Collections.sort(allDiscountTier);
            
            //save again all tier after sort
            for (int i=0;i<allDiscountTier.size();i++) {
                StoreDiscountTier currentTier = allDiscountTier.get(i);
                currentTier.setEndTotalSalesAmount(9999999.00);
                storeDiscountTierRepository.save(currentTier);
                if (i>0) {
                    StoreDiscountTier prevtier = allDiscountTier.get(i-1);
                    Double endAmount = currentTier.getStartTotalSalesAmount()-0.01;
                    prevtier.setEndTotalSalesAmount(endAmount);
                    storeDiscountTierRepository.save(prevtier);
                }
            }
        } else {        
            //check for overlap
            List<StoreDiscountTier> existingDiscountTierList = storeDiscountTierRepository.findOtherDiscountTierAmountRange(discountId, storeDiscountTier.getStartTotalSalesAmount(), storeDiscountTier.getEndTotalSalesAmount(), storeDiscountTier.getId() );
            if (existingDiscountTierList.size()>0) {
                StoreDiscountTier activeDiscountTier = existingDiscountTierList.get(0);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Overlap tier Id:"+activeDiscountTier.getId()+" StartAmount:"+activeDiscountTier.getStartTotalSalesAmount()+" EndAmount:"+activeDiscountTier.getEndTotalSalesAmount());
                List<String> errors = new ArrayList<>();
                String errorMsg = "Overlap discount tier with "+activeDiscountTier.getStartTotalSalesAmount()+" - "+activeDiscountTier.getEndTotalSalesAmount();
                errors.add(errorMsg);
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }
            //save tier
            storeDiscountTierRepository.save(storeDiscountTier);
        }
        
        allDiscountTier = storeDiscountTierRepository.findByStoreDiscountId(discountId);
        Collections.sort(allDiscountTier);
        response.setData(allDiscountTier);
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
    @DeleteMapping(path = {"/{id}"}, name = "store-discounts-tier-delete-by-id", produces = "application/json")
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
    public ResponseEntity<HttpResponse> deleteStoreDiscountTierById(HttpServletRequest request,
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

        Optional<StoreDiscountTier> optStoreDiscountTier = storeDiscountTierRepository.findById(id);

        if (!optStoreDiscountTier.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "discountTIer NOT_FOUND storeId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("discountTIer not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "discountTIer FOUND tierId: " + id);

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "discountTIer found for id: {}", id);

        StoreDiscountTier p = optStoreDiscountTier.get();
        storeDiscountTierRepository.deleteById(p.getId());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
