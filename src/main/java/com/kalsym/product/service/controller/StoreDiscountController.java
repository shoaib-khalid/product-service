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
import com.kalsym.product.service.enums.StoreDiscountType;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.model.product.ProductWithDetails;
import com.kalsym.product.service.model.store.object.CustomPageable;
import com.kalsym.product.service.repository.StoreDiscountRepository;
import com.kalsym.product.service.repository.StoreDiscountSearchSpecs;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.RegionCountriesRepository;
import com.kalsym.product.service.utility.DateTimeUtil;

import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
import com.kalsym.product.service.model.store.object.Discount;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

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
    
    @Autowired
    RegionCountriesRepository regionCountriesRepository;
    
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
        
        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        RegionCountry regionCountry = null;
        Optional<RegionCountry> t = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
        if (t.isPresent()) {
            regionCountry = t.get();
        }
        
        List<Discount> discountList = new ArrayList<Discount>();
        for (int i=0;i<storeDiscountList.size();i++) {
            StoreDiscount storeDiscount = storeDiscountList.get(i);
            Discount discount = new Discount();
            discount.setId(storeDiscount.getId());
            discount.setDiscountName(storeDiscount.getDiscountName());
            discount.setDiscountType(storeDiscount.getDiscountType());
            discount.setIsActive(storeDiscount.getIsActive());
            discount.setStoreId(storeId);
            discount.setStoreDiscountTierList(storeDiscount.getStoreDiscountTierList());
            
            //convert time to merchant timezone
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StartDate:"+storeDiscount.getStartDate().toString());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "EndDate:"+storeDiscount.getEndDate().toString());
            
            if (regionCountry!=null) {
                LocalDateTime startLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(storeDiscount.getStartDate(), ZoneId.of(regionCountry.getTimezone()) );
                LocalDateTime endLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(storeDiscount.getEndDate(), ZoneId.of(regionCountry.getTimezone()) );
                discount.setStartDate(startLocalTime.toLocalDate());
                discount.setStartTime(startLocalTime.toLocalTime());
                discount.setEndDate(endLocalTime.toLocalDate());
                discount.setEndTime(endLocalTime.toLocalTime());                
            }
            discountList.add(discount);
        }
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Found");
        response.setData(discountList);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/search"})
    public ResponseEntity<HttpResponse> searchDiscountByStoreId(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String discountName,
            @RequestParam(required = false) StoreDiscountType discountType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        List<StoreDiscount> storeDiscountList = storeDiscountRepository.findByStoreId(storeId);

        if (storeDiscountList == null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        RegionCountry regionCountry = null;
        Optional<RegionCountry> t = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
        if (t.isPresent()) {
            regionCountry = t.get();
        }
        
        StoreDiscount discountMatch = new StoreDiscount();
        Pageable pageable = PageRequest.of(page, pageSize);
        discountMatch.setStoreId(storeId);
        
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<StoreDiscount> example = Example.of(discountMatch, matcher);
        Specification discountSpec = StoreDiscountSearchSpecs.getSpecWithDatesBetween(startDate, endDate, discountName, discountType, isActive, example );
        Page<StoreDiscount> storeDiscountWithPage = storeDiscountRepository.findAll(discountSpec, pageable);
        storeDiscountList = storeDiscountWithPage.getContent();
        
        
        Discount[] discountList = new Discount[storeDiscountList.size()];
        for (int i=0;i<storeDiscountList.size();i++) {
            StoreDiscount storeDiscount = storeDiscountList.get(i);
            Discount discount = new Discount();
            discount.setId(storeDiscount.getId());
            discount.setDiscountName(storeDiscount.getDiscountName());
            discount.setDiscountType(storeDiscount.getDiscountType());
            discount.setIsActive(storeDiscount.getIsActive());
            discount.setStoreId(storeId);
            discount.setStoreDiscountTierList(storeDiscount.getStoreDiscountTierList());
            
            //convert time to merchant timezone
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, storeDiscount.getId()+" -> StartDate:"+storeDiscount.getStartDate().toString());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, storeDiscount.getId()+" -> EndDate:"+storeDiscount.getEndDate().toString());
            
            if (regionCountry!=null) {
                LocalDateTime startLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(storeDiscount.getStartDate(), ZoneId.of(regionCountry.getTimezone()) );
                LocalDateTime endLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(storeDiscount.getEndDate(), ZoneId.of(regionCountry.getTimezone()) );
                discount.setStartDate(startLocalTime.toLocalDate());
                discount.setStartTime(startLocalTime.toLocalTime());
                discount.setEndDate(endLocalTime.toLocalDate());
                discount.setEndTime(endLocalTime.toLocalTime());                
            }
            discountList[i] = discount;
        }
        
        //create custom pageable object with modified content
        CustomPageable customPageable = new CustomPageable();
        customPageable.content = discountList;
        customPageable.pageable = storeDiscountWithPage.getPageable();
        customPageable.totalPages = storeDiscountWithPage.getTotalPages();
        customPageable.totalElements = storeDiscountWithPage.getTotalElements();
        customPageable.last = storeDiscountWithPage.isLast();
        customPageable.size = storeDiscountWithPage.getSize();
        customPageable.number = storeDiscountWithPage.getNumber();
        customPageable.sort = storeDiscountWithPage.getSort();        
        customPageable.numberOfElements = storeDiscountWithPage.getNumberOfElements();
        customPageable.first  = storeDiscountWithPage.isFirst();
        customPageable.empty = storeDiscountWithPage.isEmpty();
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Found");
        response.setData(customPageable);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<HttpResponse> getDiscountById(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable String id) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId+" DiscountId:"+id);
        
        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Optional<StoreDiscount> tstoreDiscount = storeDiscountRepository.findById(id);

        if (!tstoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        StoreDiscount storeDiscount = tstoreDiscount.get();
        Discount discount = new Discount();
        discount.setId(storeDiscount.getId());
        discount.setDiscountName(storeDiscount.getDiscountName());
        discount.setDiscountType(storeDiscount.getDiscountType());
        discount.setIsActive(storeDiscount.getIsActive());
        discount.setStoreId(storeId);
        discount.setStoreDiscountTierList(storeDiscount.getStoreDiscountTierList());

        //convert time to merchant timezone
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StartDate:"+storeDiscount.getStartDate().toString());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "EndDate:"+storeDiscount.getEndDate().toString());
        
        RegionCountry regionCountry = null;
        Optional<RegionCountry> t = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
        if (t.isPresent()) {
            regionCountry = t.get();
        }
        
        if (regionCountry!=null) {
            LocalDateTime startLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(storeDiscount.getStartDate(), ZoneId.of(regionCountry.getTimezone()) );
            LocalDateTime endLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(storeDiscount.getEndDate(), ZoneId.of(regionCountry.getTimezone()) );
            discount.setStartDate(startLocalTime.toLocalDate());
            discount.setStartTime(startLocalTime.toLocalTime());
            discount.setEndDate(endLocalTime.toLocalDate());
            discount.setEndTime(endLocalTime.toLocalTime());                
        }
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Discount Found");
        response.setData(discount);
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
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
    public ResponseEntity<HttpResponse> postStoreDiscount(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @RequestBody Discount discount) {

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
        discount.setStoreId(storeId);
        
        if (discount.getStartTime()==null) {
            discount.setStartTime(LocalTime.parse("00:00:00"));
        }
        if (discount.getEndTime()==null) {
            discount.setEndTime(LocalTime.parse("23:59:59"));
        }
        
        //convert date & time to merchant timezone
        StoreDiscount storeDiscount = new StoreDiscount();
        Store store = optStore.get();
        Optional<RegionCountry> t = regionCountriesRepository.findById(store.getRegionCountryId());
        if (t.isPresent()) {
            RegionCountry regionCountry = t.get();
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store timezone:"+regionCountry.getTimezone());
            LocalDateTime startDt = LocalDateTime.of(discount.getStartDate(), discount.getStartTime());
            Date startDate = DateTimeUtil.convertToDateViaInstant(startDt, ZoneId.of(regionCountry.getTimezone()));
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StartDateTime in store timezone:"+startDate.toString());            
            storeDiscount.setStartDate(startDate);
            LocalDateTime endDt = LocalDateTime.of(discount.getEndDate(), discount.getEndTime());
            Date endDate = DateTimeUtil.convertToDateViaInstant(endDt, ZoneId.of(regionCountry.getTimezone()));
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "EndDateTime in store timezone:"+endDate.toString());
            storeDiscount.setEndDate(endDate);
        }
        
        storeDiscount.setDiscountName(discount.getDiscountName());
        storeDiscount.setDiscountType(discount.getDiscountType());
        storeDiscount.setIsActive(discount.getIsActive());
        storeDiscount.setStoreId(storeId);
        
        //check for overlap date for active discount
        if (storeDiscount.getIsActive()) {
            List<StoreDiscount> storeDiscountList = storeDiscountRepository.findAvailableDiscountDateRange(storeId, storeDiscount.getDiscountType(), storeDiscount.getStartDate(), storeDiscount.getEndDate());
            if (storeDiscountList.size()>0) {
                StoreDiscount activeDiscount = storeDiscountList.get(0);
                List<String> errors = new ArrayList<>();
                String errorMsg = "Overlap active discount. Please deactivate : "+activeDiscount.getDiscountName();
                errors.add(errorMsg);
                response.setData(errors);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Overlap discount Id:"+activeDiscount.getId()+" StartDate:"+activeDiscount.getStartDate()+" EndDate:"+activeDiscount.getEndDate());
                response.setMessage(errorMsg);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }
        }
        
        storeDiscountRepository.save(storeDiscount);
        discount.setId(storeDiscount.getId());
        
        response.setData(discount);
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {""})
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
    public ResponseEntity<HttpResponse> putStoreDiscount(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @RequestBody Discount discount) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<StoreDiscount> optStoreDiscount = storeDiscountRepository.findById(discount.getId());

        if (!optStoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount Found Object:" + optStoreDiscount);
        if (discount.getStartTime()==null) {
            discount.setStartTime(LocalTime.parse("00:00:00"));
        }
        if (discount.getEndTime()==null) {
            discount.setEndTime(LocalTime.parse("23:59:59"));
        }
        
        //convert date & time to merchant timezone
        StoreDiscount storeDiscount = new StoreDiscount();
        storeDiscount.setId(discount.getId());
        Store store = optStore.get();
        Optional<RegionCountry> t = regionCountriesRepository.findById(store.getRegionCountryId());
        if (t.isPresent()) {
            RegionCountry regionCountry = t.get();
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store timezone:"+regionCountry.getTimezone());
            LocalDateTime startDt = LocalDateTime.of(discount.getStartDate(), discount.getStartTime());
            Date startDate = DateTimeUtil.convertToDateViaInstant(startDt, ZoneId.of(regionCountry.getTimezone()));
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StartDateTime in store timezone:"+startDate.toString());            
            storeDiscount.setStartDate(startDate);
            LocalDateTime endDt = LocalDateTime.of(discount.getEndDate(), discount.getEndTime());
            Date endDate = DateTimeUtil.convertToDateViaInstant(endDt, ZoneId.of(regionCountry.getTimezone()));
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "EndDateTime in store timezone:"+endDate.toString());
            storeDiscount.setEndDate(endDate);
        }
        
        storeDiscount.setDiscountName(discount.getDiscountName());
        storeDiscount.setDiscountType(discount.getDiscountType());
        storeDiscount.setIsActive(discount.getIsActive());
        storeDiscount.setStoreId(storeId);
        
        //check for overlap date for active discount
        if (storeDiscount.getIsActive()) {
            List<StoreDiscount> storeDiscountList = storeDiscountRepository.findOtherAvailableDiscountDateRange(storeId, storeDiscount.getDiscountType(), storeDiscount.getStartDate(), storeDiscount.getEndDate(), storeDiscount.getId());
            if (storeDiscountList.size()>0) {
                StoreDiscount activeDiscount = storeDiscountList.get(0);
                List<String> errors = new ArrayList<>();
                String errorMsg = "Overlap active discount. Please deactivate : "+activeDiscount.getDiscountName();
                errors.add(errorMsg);
                response.setData(errors);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Overlap discount Id:"+activeDiscount.getId()+" StartDate:"+activeDiscount.getStartDate()+" EndDate:"+activeDiscount.getEndDate());
                response.setMessage(errorMsg);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }
        }
        
        storeDiscountRepository.save(storeDiscount);
                
        response.setData(discount);

        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @DeleteMapping(path = {"/{id}"}, name = "store-discounts-delete-by-id", produces = "application/json")
    @PreAuthorize("@customOwnerVerifier.VerifyStore(#storeId)")    
    public ResponseEntity<HttpResponse> deleteStoreDiscountById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<StoreDiscount> optStoreDiscount = storeDiscountRepository.findById(id);

        if (!optStoreDiscount.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("StoreDiscount not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "StoreDiscount FOUND storeId: " + id);

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "store found for id: {}", storeId);

        StoreDiscount p = optStoreDiscount.get();
        storeDiscountRepository.deleteById(p.getId());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
