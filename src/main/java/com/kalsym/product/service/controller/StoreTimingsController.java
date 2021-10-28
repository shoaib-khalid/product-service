package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.store.StoreTiming;
import com.kalsym.product.service.model.store.StoreTimingIdentity;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.StoreTimingsRepository;
import com.kalsym.product.service.utility.Logger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/timings")
public class StoreTimingsController {

    @Autowired
    StoreTimingsRepository storeTimingsRepository;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""}, name = "store-timings-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-timings-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreTimings(HttpServletRequest request,
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
        response.setData(storeTimingsRepository.findByStoreId(storeId));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{day}"}, name = "store-timings-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-timings-put-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> putStoreTimings(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String day,
            @RequestBody StoreTiming timingBody) {
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

        StoreTimingIdentity sti = new StoreTimingIdentity(storeId, day);
        Optional<StoreTiming> optStoreTiming = storeTimingsRepository.findById(sti);

        if (!optStoreTiming.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND store timing: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("timing not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        StoreTiming timing = optStoreTiming.get();
        if (null != timingBody.getDay()) {
            timingBody.setDay(timingBody.getDay().toUpperCase());
        }
        timing.update(timingBody);

        timingBody.setStoreId(storeId);
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(storeTimingsRepository.save(timing));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "store-timings-post")
    @PreAuthorize("hasAnyAuthority('store-timings-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreTimings(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestBody StoreTiming timingBody) {
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
        if (null != timingBody.getDay()) {
            timingBody.setDay(timingBody.getDay().toUpperCase());
        }

        timingBody.setStoreId(storeId);
        response.setStatus(HttpStatus.CREATED);
        response.setData(storeTimingsRepository.save(timingBody));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @PutMapping(path = {"/snooze"}, name = "store-timings-snooze")
    @PreAuthorize("hasAnyAuthority('store-timings-put-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> putStoreSnooze(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(required = false) Boolean isSnooze,
            @RequestParam(required = false) Integer snoozeDuration,
            @RequestParam(required = false) String snoozeReason
            ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId:" + storeId +" isSnooze:"+isSnooze+" snoozeDuration:"+snoozeDuration);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
        
        if (isSnooze) {
            //put store to offline
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cl = Calendar.getInstance();
            cl.add(Calendar.MINUTE, snoozeDuration);
            Store store = optStore.get();
            store.setIsSnooze(true);
            store.setSnoozeEndTime(cl.getTime()); 
            if (snoozeReason!=null) {
                store.setSnoozeReason(snoozeReason);
            }
            storeRepository.save(store);
             Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store:"+storeId+" put store snooze on");
        } else {
            //put store to online
            Store store = optStore.get();
            store.setIsSnooze(false);
            store.setSnoozeReason(null);
            storeRepository.save(store);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store:"+storeId+" put store snooze off");
        }
        response.setStatus(HttpStatus.ACCEPTED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
