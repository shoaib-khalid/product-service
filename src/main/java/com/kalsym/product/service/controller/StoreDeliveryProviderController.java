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

import com.kalsym.product.service.repository.StoreDeliveryProviderRepository;
import com.kalsym.product.service.repository.StoreDeliveryProviderTypeRepository;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.model.store.StoreDeliveryServiceProvider;
import com.kalsym.product.service.model.store.StoreDeliveryServiceProviderType;
import org.springframework.http.HttpStatus;
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.repository.RegionCountriesRepository;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.kalsym.product.service.model.RegionCountry;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

/**
 *
 * @author mohsi
 */
@RestController()
@RequestMapping("deliveryProvider")
public class StoreDeliveryProviderController {

    @Autowired
    private StoreDeliveryProviderRepository deliveryProviderRepository;
    
    @Autowired
    private StoreDeliveryProviderTypeRepository deliveryProviderTypeRepository;

    @Autowired
    private RegionCountriesRepository regionCountryRepository;

    @GetMapping(path = {""}, name = "delivery-provider-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-deliverydetails-get', 'all')")
    public ResponseEntity<HttpResponse> getDeliveryServiceProvider(HttpServletRequest request, 
            @RequestParam(required = true) String regionCountryId,
            @RequestParam(required = false) String deliveryType) {
        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        Optional<RegionCountry> regionCountry = regionCountryRepository.findById(regionCountryId);

        if (!regionCountry.isPresent()) {
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "Region Country Not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "Region Country found: " + regionCountry);
        
        List<StoreDeliveryServiceProviderType> deliveryProviderTypeList = null;
        if (deliveryType==null)
            deliveryProviderTypeList = deliveryProviderTypeRepository.findByRegionCountry(regionCountryId);
        else
            deliveryProviderTypeList = deliveryProviderTypeRepository.findByRegionCountryAndDeliveryType(regionCountryId, deliveryType);
        
        List<StoreDeliveryServiceProvider> storeDeliveryServiceProvider = new ArrayList<StoreDeliveryServiceProvider>();
        for (int i=0;i<deliveryProviderTypeList.size();i++) {            
            StoreDeliveryServiceProvider provider = deliveryProviderTypeList.get(i).getStoreDeliveryServiceProvider();
            storeDeliveryServiceProvider.add(provider);
        }
        response.setStatus(HttpStatus.OK);
        response.setData(storeDeliveryServiceProvider);
        
        //response.setStatus(HttpStatus.OK);
        //response.setData(deliveryProviderTypeList);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "delivery-provider-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-deliverydetails-get', 'all')")
    public ResponseEntity<HttpResponse> postDeliveryServiceProvider(HttpServletRequest request, @RequestBody StoreDeliveryServiceProvider dsp) {
        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Optional<RegionCountry> regionCountry = regionCountryRepository.findById(dsp.getRegionCountryId());
        if (!regionCountry.isPresent()) {
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "Region Country Not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "Region Country found: " + regionCountry);
        response.setData(deliveryProviderRepository.save(dsp));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"{id}"}, name = "delivery-provider-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-deliverydetails-get', 'all')")
    public ResponseEntity<HttpResponse> putDeliveryServiceProvider(HttpServletRequest request, @PathVariable String id, @RequestBody StoreDeliveryServiceProvider dsp) {
        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Optional<RegionCountry> regionCountry = regionCountryRepository.findById(dsp.getRegionCountryId());
        if (!regionCountry.isPresent()) {
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "Region Country Not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "Region Country found: " + regionCountry);
        dsp.setId(id);
        response.setData(deliveryProviderRepository.save(dsp));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}