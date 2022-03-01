package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.store.StoreDeliveryPeriod;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.DeliveryPeriod;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.DeliveryPeriodRepository;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.repository.StoreDeliveryPeriodsRepository;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/deliveryperiods")
public class StoreDeliveryPeriodsController {

    @Autowired
    StoreDeliveryPeriodsRepository storeOptionsRepository;

    @Autowired
    StoreRepository storeRepository;
    
    @Autowired
    DeliveryPeriodRepository deliveryPeriodRepository;
    
    @GetMapping(path = {""}, name = "store-deliveryoptions-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-deliveryoptions-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreDeliveryOptions(HttpServletRequest request,
            @PathVariable String storeId) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);
        
        List<StoreDeliveryPeriod> storeDeliveryOptionList = null;
        if (storeId.equals("null")) {
            storeDeliveryOptionList = SetDefaultDeliveryOptions(storeId, deliveryPeriodRepository);
        } else {
        
            Optional<Store> optStore = storeRepository.findById(storeId);

            if (!optStore.isPresent()) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
                response.setStatus(HttpStatus.NOT_FOUND);
                response.setError("store not found");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
            storeDeliveryOptionList = storeOptionsRepository.findByStoreId(storeId);
            if (storeDeliveryOptionList.isEmpty()) {                     
                storeDeliveryOptionList = SetDefaultDeliveryOptions(storeId, deliveryPeriodRepository);
            } 
        }
        response.setStatus(HttpStatus.OK);
        response.setData(storeDeliveryOptionList);
        return ResponseEntity.status(response.getStatus()).body(response);
    }   

    @PostMapping(path = {""}, name = "store-deliveryoptions-post")
    @PreAuthorize("hasAnyAuthority('store-deliveryoptions-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreDeliveryOptions(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestBody List<StoreDeliveryPeriod> deliveryDetailBody) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
        
        List<StoreDeliveryPeriod> storeDeliveryOptionList = storeOptionsRepository.findByStoreId(storeId);
        if (storeDeliveryOptionList.isEmpty()) {                     
            for (int i=0;i<deliveryDetailBody.size();i++) {
                StoreDeliveryPeriod deliveryOption =  deliveryDetailBody.get(i);
                storeOptionsRepository.save(deliveryOption);
            }
        } else {
            for (int i=0;i<deliveryDetailBody.size();i++) {
                StoreDeliveryPeriod deliveryOption =  deliveryDetailBody.get(i);            
                storeOptionsRepository.UpdateStoreDeliveryOption(storeId, deliveryOption.getDeliveryPeriod(), deliveryOption.getEnabled());
            }
        }
        
        List<StoreDeliveryPeriod> newStoreDeliveryOptionList = storeOptionsRepository.findByStoreId(storeId);
        if (newStoreDeliveryOptionList.isEmpty()) {                     
            newStoreDeliveryOptionList = SetDefaultDeliveryOptions(storeId, deliveryPeriodRepository);
        }
        response.setData(newStoreDeliveryOptionList);        
        response.setStatus(HttpStatus.CREATED);
        
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    public static List<StoreDeliveryPeriod> SetDefaultDeliveryOptions(String storeId, DeliveryPeriodRepository deliveryPeriodRepository) {
        List<StoreDeliveryPeriod> storeDeliveryList = new ArrayList<>();
        
        List<DeliveryPeriod> deliveryPeriodList = deliveryPeriodRepository.findAll();
        for (int i=0;i<deliveryPeriodList.size();i++) {
            DeliveryPeriod deliveryPeriod = deliveryPeriodList.get(i);
            
            StoreDeliveryPeriod deliveryOption = new StoreDeliveryPeriod();
            deliveryOption.setDeliveryPeriod(deliveryPeriod.getId());
            deliveryOption.setStoreId(storeId);
            deliveryOption.setEnabled(Boolean.FALSE);
            deliveryOption.setDeliveryPeriodDetails(deliveryPeriod);
            storeDeliveryList.add(deliveryOption);
        }
        
        return storeDeliveryList;
    }
    
}
