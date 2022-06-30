package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.kalsym.product.service.model.store.PlatformConfig;
import com.kalsym.product.service.repository.PlatformConfigRepository;
import com.kalsym.product.service.utility.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/platformconfig")
public class PlatformConfigController {

    @Autowired
    PlatformConfigRepository platformConfigRepository;

    @Value("${asset.service.url}")
    String assetServiceUrl;

    @GetMapping(path = {""}, name = "region-countries-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-countries-get', 'all')")
    public ResponseEntity<HttpResponse> getConfigbyDomain(HttpServletRequest request,
            @RequestParam(required = false) String domain) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        List<PlatformConfig> configList = platformConfigRepository.findByDomain(domain);

        //to set the asset url 

        for(PlatformConfig platformConfig : configList){

            PlatformConfig getPlatFormConfig = platformConfig;
            getPlatFormConfig.setPlatformLogo(assetServiceUrl+platformConfig.getPlatformLogo());
            getPlatFormConfig.setPlatformLogoDark(assetServiceUrl+platformConfig.getPlatformLogoDark());
            getPlatFormConfig.setPlatformFavIcon(assetServiceUrl+platformConfig.getPlatformFavIcon());
            getPlatFormConfig.setPlatformFavIcon32(assetServiceUrl+platformConfig.getPlatformFavIcon32());
            
        }

        if (configList.isEmpty()) {
            response.setStatus(HttpStatus.NOT_FOUND);
        } else {
            response.setData(configList);
            response.setStatus(HttpStatus.OK);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
