package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.kalsym.product.service.model.RegionCountryState;
import com.kalsym.product.service.repository.RegionCountryStatesRepository;
import com.kalsym.product.service.utility.Logger;
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
 * GET /region-countries GET /region-countries/{id}
 *
 * POST /region-countries
 *
 * DELETE /region-countries/{id}
 *
 * PUT /region-countries/{id}
 *
 *
 *
 *
 * GET /region-countries/{storeId}/products
 *
 * POST /region-countries/{storeId}/products
 *
 * GET /region-countries/{storeId}/regionCountry-categories
 *
 * POST /region-countries/{storeId}/regionCountry-categories
 */
@RestController()
@RequestMapping("/region-country-state")
public class RegionCountryStatesController {

    @Autowired
    RegionCountryStatesRepository regionCountryStatesRepository;

    @GetMapping(path = {""}, name = "region-country-states-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-country-states-get', 'all')")
    public ResponseEntity<HttpResponse> getRegionCountryState(HttpServletRequest request,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String regionCountryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        RegionCountryState regionCountry = new RegionCountryState();

        regionCountry.setName(name);
        regionCountry.setRegionCountryId(regionCountryId);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<RegionCountryState> example = Example.of(regionCountry, matcher);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);
        response.setData(regionCountryStatesRepository.findAll(example, pageable));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "region-country-states-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-country-states-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getRegionCountryStateById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<RegionCountryState> optRegionCountryState = regionCountryStatesRepository.findById(id);

        if (!optRegionCountryState.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        response.setData(optRegionCountryState.get());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "region-country-states-post")
    @PreAuthorize("hasAnyAuthority('region-country-states-post', 'all')")
    public ResponseEntity<HttpResponse> postRegionCountryState(HttpServletRequest request,
            @Valid @RequestBody RegionCountryState bodyRegionCountryState) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "region-country-states-post", "");
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, bodyRegionCountryState.toString(), "");

        response.setStatus(HttpStatus.CREATED);
        RegionCountryState savedRegionCountryState = null;
        try {
            savedRegionCountryState = regionCountryStatesRepository.save(bodyRegionCountryState);
        } catch (Exception exp) {
            Logger.application.error("Error in creating regionCountry", exp);
            response.setMessage(exp.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "regionCountry created with id: " + savedRegionCountryState.getId());
        response.setData(savedRegionCountryState);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "region-country-states-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-country-states-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putRegionCountryStateById(HttpServletRequest request,
            @PathVariable(required = true) String id,
            @Valid @RequestBody RegionCountryState bodyRegionCountryState
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<RegionCountryState> optRegionCountryState = regionCountryStatesRepository.findById(id);

        if (!optRegionCountryState.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);

        RegionCountryState regionCountry = optRegionCountryState.get();

        regionCountry.update(bodyRegionCountryState);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "updated regionCountry with id: " + id);
        response.setData(regionCountryStatesRepository.save(regionCountry));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "region-country-states-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-country-states-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteRegionCountryStateById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<RegionCountryState> optRegionCountryState = regionCountryStatesRepository.findById(id);

        if (!optRegionCountryState.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        regionCountryStatesRepository.deleteById(id);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleted regionCountry with id: " + id);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
