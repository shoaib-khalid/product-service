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
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.StoreCommission;
import com.kalsym.product.service.repository.StateDeliveryChargeRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.model.store.StateDeliveryCharge;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 *
 * @author mohsi
 */
@RestController
@RequestMapping("/stores/{storeId}/stateDeliveryCharge")
public class StateDeliveryChargesController {

    @Autowired
    StateDeliveryChargeRepository stateDeliveryChargeRepository;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {"{id}"})
    public ResponseEntity<HttpResponse> getStoreDeliveryCharge(HttpServletRequest request,
            @PathVariable(required = true) String id,
            @PathVariable(required = true) String storeId) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<StateDeliveryCharge> stateDCharge = stateDeliveryChargeRepository.findById(id);

        if (!stateDCharge.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Found");
        response.setData(stateDCharge);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""})
    public ResponseEntity<HttpResponse> postStoreDeliveryCharge(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @RequestBody StateDeliveryCharge stateDelivery) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Found Object:" + optStore);
        stateDelivery.setStoreId(storeId);
        response.setData(stateDeliveryChargeRepository.save(stateDelivery));
        response.setStatus(HttpStatus.CREATED);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"{id}"})
    public ResponseEntity<HttpResponse> putStoreDeliveryCharge(HttpServletRequest request,
            @PathVariable(required = true) String id,
            @PathVariable(required = true) String storeId,
            @RequestBody StateDeliveryCharge stateDelivery) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<StateDeliveryCharge> stateDCharge = stateDeliveryChargeRepository.findById(id);

        if (!stateDCharge.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Found Object:" + optStore);
        stateDelivery.setStoreId(storeId);
        stateDelivery.setId(id);
        response.setData(stateDeliveryChargeRepository.save(stateDelivery));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {""})
    public ResponseEntity<HttpResponse> getDeliveryChargesByStore(HttpServletRequest request,
            @PathVariable(required = true) String storeId) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Found");

        List<StateDeliveryCharge> stateChargesStore = stateDeliveryChargeRepository.findByStoreId(storeId);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "State Charges of store:" + stateChargesStore);

        response.setData(stateChargesStore);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"{id}"})
    public ResponseEntity<HttpResponse> deleteStateDeliveryCharge(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @PathVariable(required = true) String id) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Id recieved: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Not Found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Found");

        stateDeliveryChargeRepository.deleteById(id);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix,
                "State Charge with id: " + id + " deleted successfully.");

        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
