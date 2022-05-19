package com.kalsym.product.service.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.kalsym.product.service.model.PromoText;
import com.kalsym.product.service.service.PromoTextService;
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

@RestController
@RequestMapping("/promo-text")
public class PromoTextController {
    
    @Autowired
    PromoTextService promoTextService;

    //Get By Query WITH Pagination
    // @GetMapping(value={""})
    @GetMapping(path = {""}, name = "promo-text-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('promo-text-get', 'all')")
    public ResponseEntity<HttpResponse> getPromoText(
        HttpServletRequest request,
        @RequestParam(required = false) String verticalCode,
        @RequestParam(required = false) String eventId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {

        Page<PromoText> body = promoTextService.getByQueryPromoText(page,pageSize,verticalCode,eventId);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    
    /// Get By Id
    // @GetMapping(value="/{eventId}")
    @GetMapping(path = {"/{id}"}, name = "promo-text-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('promo-text-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getPromoByEventId(
        HttpServletRequest request,
        @PathVariable(value = "id") String id) {

            HttpResponse response = new HttpResponse(request.getRequestURI());

        Optional<PromoText> body = promoTextService.getPromoTextById(id);
        if(body.isPresent()){

            response.setData(body);
            response.setStatus(HttpStatus.OK);

        } else{
            response.setStatus(HttpStatus.NOT_FOUND);

        }
        
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
