package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kalsym.product.service.model.RegionVertical;
import com.kalsym.product.service.repository.RegionVerticalRepository;
import com.kalsym.product.service.utility.Logger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7cu
 *
 * GET /region-verticals GET /region-verticals/{id}
 *
 * POST /region-verticals
 *
 * DELETE /region-verticals/{id}
 *
 * PUT /region-verticals/{id}
 *
 *
 *
 *
 * GET /region-verticals/{storeId}/products
 *
 * POST /region-verticals/{storeId}/products
 *
 * GET /region-verticals/{storeId}/regionVertical-categories
 *
 * POST /region-verticals/{storeId}/regionVertical-categories
 */
@RestController()
@RequestMapping("/region-verticals")
public class RegionVerticalController {
    
    @Autowired
    RegionVerticalRepository regionVerticalRepository;
    
    @GetMapping(path = {""}, name = "region-verticals-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-verticals-get', 'all')")
    public ResponseEntity<HttpResponse> getRegionVerticles(HttpServletRequest request) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");
        RegionVertical regionVertical = new RegionVertical();
        List<RegionVertical> regionVerticles = regionVerticalRepository.findAll();
        response.setData(regionVerticles);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/{id}"}, name = "region-verticals-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-verticals-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");
        
        Optional<RegionVertical> optStore = regionVerticalRepository.findById(id);
        
        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        response.setData(optStore.get());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @PostMapping(path = {""}, name = "region-verticals-post")
    @PreAuthorize("hasAnyAuthority('region-verticals-post', 'all')")
    public ResponseEntity<HttpResponse> postStore(HttpServletRequest request,
            @Valid @RequestBody RegionVertical bodyStore) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "region-verticals-post", "");
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, bodyStore.toString(), "");
        
        response.setStatus(HttpStatus.CREATED);
        RegionVertical savedRegionVertical = null;
        try {
            savedRegionVertical = regionVerticalRepository.save(bodyStore);
        } catch (Exception exp) {
            Logger.application.error("Error in creating regionVertical", exp);
            response.setMessage(exp.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "regionVertical created with id: " + savedRegionVertical.getCode());
        response.setData(savedRegionVertical);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping(path = {"/{id}"}, name = "region-verticals-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-verticals-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id,
            @Valid @RequestBody RegionVertical bodyStore
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");
        
        Optional<RegionVertical> optStore = regionVerticalRepository.findById(id);
        
        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        
        RegionVertical regionVertical = optStore.get();
        
        regionVertical.update(bodyStore);
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "updated regionVertical with id: " + id);
        response.setData(regionVerticalRepository.save(regionVertical));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @DeleteMapping(path = {"/{id}"}, name = "region-verticals-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-verticals-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");
        
        Optional<RegionVertical> optStore = regionVerticalRepository.findById(id);
        
        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        regionVerticalRepository.deleteById(id);
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleted regionVertical with id: " + id);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
}
