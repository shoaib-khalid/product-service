package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.store.StoreTiming;
import com.kalsym.product.service.model.store.StoreTimingIdentity;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.StoreSnooze;
import com.kalsym.product.service.repository.RegionCountriesRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.StoreTimingsRepository;
import com.kalsym.product.service.utility.DateTimeUtil;
import com.kalsym.product.service.utility.Logger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
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
    
    @Autowired
    RegionCountriesRepository regionCountriesRepository;

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
    @PreAuthorize("hasAnyAuthority('store-timings-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putStoreSnooze(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(required = true) Boolean isSnooze,
            @RequestParam(required = true) Integer snoozeDuration,
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
            //store.setIsSnooze(true);
            store.setSnoozeStartTime(Calendar.getInstance().getTime()); 
            store.setSnoozeEndTime(cl.getTime()); 
            if (snoozeReason!=null) {
                store.setSnoozeReason(snoozeReason);
            } else {
                store.setSnoozeReason(null);
            }
            storeRepository.save(store);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store:"+storeId+" put store snooze on");
        } else {
            //put store to online
            Store store = optStore.get();
            //store.setIsSnooze(false);
            store.setSnoozeStartTime(null); 
            store.setSnoozeEndTime(null); 
            store.setSnoozeReason(null);
            storeRepository.save(store);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store:"+storeId+" put store snooze off");
        }
        response.setStatus(HttpStatus.ACCEPTED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/snooze"}, name = "store-timings-snooze", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-timings-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreSnooze(HttpServletRequest request,
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
        
        StoreSnooze storeSnooze=new StoreSnooze();
        Store store = optStore.get();
        if (store.getSnoozeStartTime()!=null && store.getSnoozeEndTime()!=null) {
            int result = store.getSnoozeEndTime().compareTo(Calendar.getInstance().getTime());
            if (result < 0) {
                //snooze already expired
                storeSnooze.isSnooze=false;
            } else {
                storeSnooze.isSnooze=true;
                storeSnooze.snoozeReason=store.getSnoozeReason();
                
                //convert time to merchant timezone
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Snooze End Time:"+store.getSnoozeEndTime().toString());
                Optional<RegionCountry> t = regionCountriesRepository.findById(store.getRegionCountryId());
                if (t.isPresent()) {
                    RegionCountry regionCountry = t.get(); 
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store timezone:"+regionCountry.getTimezone());
                    LocalDateTime startTime = DateTimeUtil.convertToLocalDateTimeViaInstant(store.getSnoozeStartTime(), ZoneId.of(regionCountry.getTimezone()));
                    LocalDateTime endTime = DateTimeUtil.convertToLocalDateTimeViaInstant(store.getSnoozeEndTime(), ZoneId.of(regionCountry.getTimezone()));
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Snooze End Time in store timezone:"+endTime);
                    storeSnooze.snoozeStartTime = startTime; 
                    storeSnooze.snoozeEndTime = endTime;
                }
            }
        } else {
            storeSnooze.isSnooze=false;
        }
        response.setStatus(HttpStatus.OK);
        response.setData(storeSnooze);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
