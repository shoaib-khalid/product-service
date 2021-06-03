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
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.repository.RegionCountriesRepository;
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
@RequestMapping("/region-countries")
public class RegionCountryController {

    @Autowired
    RegionCountriesRepository regionCountryRepository;

    @GetMapping(path = {""}, name = "region-countries-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-countries-get', 'all')")
    public ResponseEntity<HttpResponse> getRegionCountry(HttpServletRequest request,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        RegionCountry regionCountry = new RegionCountry();

        regionCountry.setName(name);
        regionCountry.setRegion(region);
        regionCountry.setCurrency(currency);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<RegionCountry> example = Example.of(regionCountry, matcher);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);
        response.setData(regionCountryRepository.findAll(example, pageable));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "region-countries-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-countries-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getRegionCountryById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<RegionCountry> optRegionCountry = regionCountryRepository.findById(id);

        if (!optRegionCountry.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        response.setData(optRegionCountry.get());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "region-countries-post")
    @PreAuthorize("hasAnyAuthority('region-countries-post', 'all')")
    public ResponseEntity<HttpResponse> postRegionCountry(HttpServletRequest request,
            @Valid @RequestBody RegionCountry bodyRegionCountry) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "region-countries-post", "");
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, bodyRegionCountry.toString(), "");

        response.setStatus(HttpStatus.CREATED);
        RegionCountry savedRegionCountry = null;
        try {
            savedRegionCountry = regionCountryRepository.save(bodyRegionCountry);
        } catch (Exception exp) {
            Logger.application.error("Error in creating regionCountry", exp);
            response.setMessage(exp.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "regionCountry created with id: " + savedRegionCountry.getId());
        response.setData(savedRegionCountry);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "region-countries-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-countries-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putRegionCountryById(HttpServletRequest request,
            @PathVariable(required = true) String id,
            @Valid @RequestBody RegionCountry bodyRegionCountry
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<RegionCountry> optRegionCountry = regionCountryRepository.findById(id);

        if (!optRegionCountry.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);

        RegionCountry regionCountry = optRegionCountry.get();

        regionCountry.update(bodyRegionCountry);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "updated regionCountry with id: " + id);
        response.setData(regionCountryRepository.save(regionCountry));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "region-countries-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-countries-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteRegionCountryById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<RegionCountry> optRegionCountry = regionCountryRepository.findById(id);

        if (!optRegionCountry.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        regionCountryRepository.deleteById(id);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleted regionCountry with id: " + id);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
