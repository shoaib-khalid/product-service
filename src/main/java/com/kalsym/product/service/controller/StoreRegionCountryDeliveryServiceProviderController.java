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
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.utility.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.utility.HttpResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.model.store.StoreRegionCountryDeliveryServiceProvider;
import com.kalsym.product.service.repository.StoreRegionCountryDeliveryServiceProviderRepository;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author mohsi
 */
@RestController()
@RequestMapping("/stores/{storeId}/deliveryServiceProvider")
public class StoreRegionCountryDeliveryServiceProviderController {

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreRegionCountryDeliveryServiceProviderRepository sdspr;

    @PostMapping(path = "{deliverySpId}", name = "post-store-region-delivery-service-provider", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-variants-get', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreRegionCountryDeliveryServiceProvider(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String deliverySpId) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Optional<Store> optStore = storeRepository.findById(storeId);
        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
        StoreRegionCountryDeliveryServiceProvider sdsp = new StoreRegionCountryDeliveryServiceProvider();
        sdsp.setStoreId(storeId);
        sdsp.setDeliverySpId(deliverySpId);
        response.setStatus(HttpStatus.OK);
        response.setData(sdspr.save(sdsp));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = "{deliverySpId}", name = "delete-store-region-delivery-service-provider", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-variants-get', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreRegionCountryDeliveryServiceProviderController(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String deliverySpId) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Optional<Store> optStore = storeRepository.findById(storeId);
        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
        StoreRegionCountryDeliveryServiceProvider sdsp = sdspr.findByDeliverySpIdAndStoreId(deliverySpId, storeId);
        sdspr.deleteById(sdsp.getId());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = "", name = "delete-store-region-delivery-service-provider", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-variants-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreRegionCountryDeliveryServiceProviderController(HttpServletRequest request,
            @RequestParam String storeId,
            @RequestParam(required = false) String deliverySpId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Optional<Store> optStore = storeRepository.findById(storeId);
        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
        StoreRegionCountryDeliveryServiceProvider sdsp = new StoreRegionCountryDeliveryServiceProvider();
        sdsp.setDeliverySpId(deliverySpId);
        sdsp.setStoreId(storeId);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<StoreRegionCountryDeliveryServiceProvider> example = Example.of(sdsp, matcher);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<StoreRegionCountryDeliveryServiceProvider> fetchedPage = sdspr.findAll(example, pageable);
        response.setData(fetchedPage);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = "{id}", name = "update-store-region-delivery-service-provider", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-variants-get', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> updateStoreRegionCountryDeliveryServiceProviderController(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable(required = false) String id,
            @RequestParam String deliverySpId) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Optional<Store> optStore = storeRepository.findById(storeId);
        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        StoreRegionCountryDeliveryServiceProvider sdsp = new StoreRegionCountryDeliveryServiceProvider();
        sdsp.setId(id);
        sdsp.setDeliverySpId(deliverySpId);
        sdsp.setStoreId(storeId);
        response.setData(sdspr.save(sdsp));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = "all", name = "delete-store-region-delivery-service-provider", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-variants-get', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreDeliveryServiceProvider(HttpServletRequest request,
            @PathVariable String storeId) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Optional<Store> optStore = storeRepository.findById(storeId);
        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
        List<StoreRegionCountryDeliveryServiceProvider> sdsps = sdspr.findByStoreId(storeId);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Deleting previous delivery providers linked to store");
        for (StoreRegionCountryDeliveryServiceProvider sdsp : sdsps) {
            sdspr.deleteById(sdsp.getId());
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Successfully deleted previous delivery providers linked to store");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
