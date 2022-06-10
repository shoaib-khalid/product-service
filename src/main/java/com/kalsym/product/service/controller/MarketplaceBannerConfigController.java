package com.kalsym.product.service.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import com.kalsym.product.service.model.MarketplaceBannerConfig;
import com.kalsym.product.service.service.MarketPlaceBannerConfigService;
import com.kalsym.product.service.utility.HttpResponse;

@RestController
@RequestMapping("/banner-config")
public class MarketplaceBannerConfigController {
    
    @Autowired
    MarketPlaceBannerConfigService marketPlaceBannerConfigService;

    @GetMapping(path = {""}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getBannerConfig(
        HttpServletRequest request,
        @RequestParam(required = false) String regionCountryId
    ) {

        List<MarketplaceBannerConfig> body = marketPlaceBannerConfigService.getQueryRegionCountryId(regionCountryId);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

}

