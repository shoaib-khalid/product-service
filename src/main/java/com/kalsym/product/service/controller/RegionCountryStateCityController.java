package com.kalsym.product.service.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.kalsym.product.service.model.RegionCountryStateCity;
import com.kalsym.product.service.service.RegionCountryStateCityService;
import com.kalsym.product.service.utility.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/region-country-state-city")
public class RegionCountryStateCityController {
    
    @Autowired
    RegionCountryStateCityService regionCountryStateCityService;
    //Get By Query WITH Pagination
    // @GetMapping(value={""})
    @GetMapping(path = {""}, name = "region-country-state-city-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-country-state-city-get', 'all')")
    public ResponseEntity<HttpResponse> getRegionCountryStateCity(
        HttpServletRequest request,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String state,
        @RequestParam(required = false) String city,
        @RequestParam(required = false, defaultValue = "name") String sortByCol,
        @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder
    ) throws Exception {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        try{
            System.out.println("IMAN CHECKING :::"+state);

            List<RegionCountryStateCity> body = regionCountryStateCityService.getByQueryRegionCountryStateCity(country,state,city,sortByCol,sortingOrder);
        
            response.setData(body);
            response.setStatus(HttpStatus.OK);

        } catch (Throwable e) {
            // response.setData(e.getMessage());
            System.out.println("ERRROR:::"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(response.getStatus()).body(response);

    }
}
