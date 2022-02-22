package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.DeliveryPeriod;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.store.StoreDeliveryPeriod;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.StoreRepository;
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

    @GetMapping(path = {""}, name = "store-deliveryoptions-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-deliveryoptions-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreDeliveryOptions(HttpServletRequest request,
            @PathVariable String storeId) {
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
        List<StoreDeliveryPeriod> storeDeliveryOptionList = storeOptionsRepository.findByStoreId(storeId);
        if (storeDeliveryOptionList.isEmpty()) {                     
            storeDeliveryOptionList = SetDefaultDeliveryOptions(storeId);
        } else {
            for (int i=0;i<storeDeliveryOptionList.size();i++) {
                StoreDeliveryPeriod storeDeliveryPeriod = storeDeliveryOptionList.get(i);
                storeDeliveryPeriod.setDescription(SetDeliveryPeriodDescription(storeDeliveryPeriod.getDeliveryPeriod()));
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
            newStoreDeliveryOptionList = SetDefaultDeliveryOptions(storeId);
        }
        response.setData(newStoreDeliveryOptionList);        
        response.setStatus(HttpStatus.CREATED);
        
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    public static List<StoreDeliveryPeriod> SetDefaultDeliveryOptions(String storeId) {
        List<StoreDeliveryPeriod> storeDeliveryList = new ArrayList<>();
        
        StoreDeliveryPeriod deliveryOption = new StoreDeliveryPeriod();
        deliveryOption.setDeliveryPeriod(DeliveryPeriod.EXPRESS);
        deliveryOption.setStoreId(storeId);
        deliveryOption.setEnabled(Boolean.FALSE);
        deliveryOption.setDescription(SetDeliveryPeriodDescription(DeliveryPeriod.EXPRESS));
        storeDeliveryList.add(deliveryOption);
        
        StoreDeliveryPeriod deliveryOption2 = new StoreDeliveryPeriod();
        deliveryOption2.setDeliveryPeriod(DeliveryPeriod.FOURHOURS);
        deliveryOption2.setStoreId(storeId);
        deliveryOption2.setEnabled(Boolean.FALSE);
        deliveryOption.setDescription(SetDeliveryPeriodDescription(DeliveryPeriod.FOURHOURS));
        storeDeliveryList.add(deliveryOption2);
        
        StoreDeliveryPeriod deliveryOption3 = new StoreDeliveryPeriod();
        deliveryOption3.setDeliveryPeriod(DeliveryPeriod.NEXTDAY);
        deliveryOption3.setStoreId(storeId);
        deliveryOption3.setEnabled(Boolean.FALSE);
        deliveryOption.setDescription(SetDeliveryPeriodDescription(DeliveryPeriod.NEXTDAY));
        storeDeliveryList.add(deliveryOption3);
        
        StoreDeliveryPeriod deliveryOption4 = new StoreDeliveryPeriod();
        deliveryOption4.setDeliveryPeriod(DeliveryPeriod.FOURDAYS);
        deliveryOption4.setStoreId(storeId);
        deliveryOption4.setEnabled(Boolean.FALSE);
        deliveryOption.setDescription(SetDeliveryPeriodDescription(DeliveryPeriod.FOURDAYS));
        storeDeliveryList.add(deliveryOption4);
       
        /*for (int i=0;i<storeDeliveryList.size();i++) {
            StoreDeliveryOption defaultOption = storeDeliveryList.get(i);
            for (int x=0;x<availableDeliveryOption.size();x++) {
                if (defaultOption.getDeliveryOption()==availableDeliveryOption.get(x).getDeliveryOption()) {
                    defaultOption.setEnabled(Boolean.TRUE);
                }
            }
        }*/
        return storeDeliveryList;
    }
    
    private static String SetDeliveryPeriodDescription(DeliveryPeriod deliveryPeriod) {
        if (deliveryPeriod==DeliveryPeriod.EXPRESS) {
            return "Express";
        } else if (deliveryPeriod==DeliveryPeriod.FOURHOURS) {
           return "Four Hours"; 
        } else if (deliveryPeriod==DeliveryPeriod.NEXTDAY) {
           return "Next Day"; 
        } else if (deliveryPeriod==DeliveryPeriod.FOURDAYS) {
           return "Four Days"; 
        } else {
            return "";
        }                
    }

}
