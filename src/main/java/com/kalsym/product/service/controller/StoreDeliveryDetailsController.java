package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.store.StoreDeliveryDetail;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.StoreDeliveryDetailsRepository;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
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

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/deliverydetails")
public class StoreDeliveryDetailsController {

    @Autowired
    StoreDeliveryDetailsRepository storeTimingsRepository;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""}, name = "store-deliverydetails-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-deliverydetails-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreDeliveryDetails(HttpServletRequest request,
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

        response.setStatus(HttpStatus.OK);
        response.setData(storeTimingsRepository.findById(storeId));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     *
     * @param request
     * @param storeId
     * @param deliveryDetailBody
     * @return
     */
    @PutMapping(path = {""}, name = "store-deliverydetails-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-deliverydetails-put-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> putStoreDeliveryDetails(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestBody StoreDeliveryDetail deliveryDetailBody) {
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


        Optional<StoreDeliveryDetail> optStoreDeliveryDetail = storeTimingsRepository.findById(storeId);

        if (!optStoreDeliveryDetail.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND store deliveryDetail: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("deliveryDetail not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        StoreDeliveryDetail deliveryDetail = optStoreDeliveryDetail.get();
       
        deliveryDetail.update(deliveryDetailBody);

        deliveryDetailBody.setStoreId(storeId);
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(storeTimingsRepository.save(deliveryDetail));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "store-deliverydetails-post")
    @PreAuthorize("hasAnyAuthority('store-deliverydetails-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreDeliveryDetails(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestBody StoreDeliveryDetail deliveryDetailBody) {
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
        

        deliveryDetailBody.setStoreId(storeId);
        response.setStatus(HttpStatus.CREATED);
        response.setData(storeTimingsRepository.save(deliveryDetailBody));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
