package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.PlatformOgTag;
import com.kalsym.product.service.utility.HttpResponse;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import com.kalsym.product.service.repository.PlatformOgTagRepository;
import com.kalsym.product.service.utility.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/platform-og-tag")
public class PlatfromOgTagController {

    @Autowired
    PlatformOgTagRepository platformOgTagRepository;

    @GetMapping(path = {""}, name = "platform-og-tag-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('platform-og-tag-get', 'all')")
    public ResponseEntity<HttpResponse> getPlatformOgTag(HttpServletRequest request,
            @RequestParam(required = false) String platformId) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        List<PlatformOgTag> platfromOgTagList = platformOgTagRepository.findByPlatformId(platformId);

        response.setData(platfromOgTagList);
        response.setStatus(HttpStatus.OK);

        return ResponseEntity.status(response.getStatus()).body(response);
    }


}

