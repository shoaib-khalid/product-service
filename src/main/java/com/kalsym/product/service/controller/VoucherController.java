/*
 * Copyright (C) 2021 mohsi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.VoucherStatus;
import com.kalsym.product.service.enums.VoucherType;
import com.kalsym.product.service.repository.VoucherSearchSpecs;

import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import com.kalsym.product.service.model.store.Voucher;
import com.kalsym.product.service.repository.VoucherRepository;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author mohsin
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Autowired
    VoucherRepository voucherRepository;
  
    @GetMapping(path = {"/available"})
    public ResponseEntity<HttpResponse> getAvailableVoucher(HttpServletRequest request,
            @RequestParam(required = false) VoucherType voucherType,
            @RequestParam(required = false) String storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
            )
    {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "voucherType:" + voucherType+" storeId:"+storeId);
      
        Voucher voucherMatch = new Voucher();
        voucherMatch.setStatus(VoucherStatus.ACTIVE);
        Pageable pageable = PageRequest.of(page, pageSize);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Voucher> example = Example.of(voucherMatch, matcher);
        
        Specification voucherSpec = VoucherSearchSpecs.getSpecWithDatesBetween(new Date(), voucherType, storeId, example );
        Page<Voucher> voucherWithPage = voucherRepository.findAll(voucherSpec, pageable);
        
        response.setStatus(HttpStatus.OK);
        response.setData(voucherWithPage);
        
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    


}
