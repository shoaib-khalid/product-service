package com.kalsym.product.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalsym.product.service.model.MarketplacePopUp;
import com.kalsym.product.service.service.MarketplacePopUpService;
import com.kalsym.product.service.utility.HttpResponse;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/marketplace-popup")
public class MarketplacePopUpController {

    @Autowired
    MarketplacePopUpService marketplacePopUpService;

    @Value("${asset.service.url}")
    String assetServiceUrl;

    @GetMapping(path = {""}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getPopUp(
        HttpServletRequest request,
        @RequestParam(required = false) String regionCountryId,
        @RequestParam(required = false) String type,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int pageSize,
        @RequestParam(required = false,defaultValue = "sequence") String sortByCol,
        @RequestParam(required = false,defaultValue = "ASC") Sort.Direction sortingOrder
    ) {

        List<MarketplacePopUp> body = marketplacePopUpService.getListMarketplacePopup(regionCountryId,type,page,pageSize,sortByCol,sortingOrder);

        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    // @GetMapping(path = {""}, name = "store-customers-get")
    // @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    // public ResponseEntity<HttpResponse> getPopUpWithPaging(
    //     HttpServletRequest request,
    //     @RequestParam(required = false) String regionCountryId,
    //     @RequestParam(required = false) String type,
    //     @RequestParam(defaultValue = "0") int page,
    //     @RequestParam(defaultValue = "10") int pageSize,
    //     @RequestParam(required = false,defaultValue = "sequence") String sortByCol,
    //     @RequestParam(required = false,defaultValue = "ASC") Sort.Direction sortingOrder
    // ) {

    //     Page<MarketplacePopUp> body = marketplacePopUpService.getMarketPlacePopUpConfig(regionCountryId,type,page,pageSize,sortByCol,sortingOrder);

    //     HttpResponse response = new HttpResponse(request.getRequestURI());
    //     response.setData(body);
    //     response.setStatus(HttpStatus.OK);
    //     return ResponseEntity.status(response.getStatus()).body(response);

    // }

    
}
