package com.kalsym.product.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.product.AddOnTemplateItem;
import com.kalsym.product.service.model.request.AddOnTemplateItemRequest;
import com.kalsym.product.service.utility.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import com.kalsym.product.service.utility.Logger;
import javax.validation.Valid;


import com.kalsym.product.service.service.AddOnTemplateItemService;

@RestController()
@RequestMapping("/addon-template-item")
public class AddOnTemplateItemController {
    
    @Autowired
    AddOnTemplateItemService addOnTemplateItemService;
    
    @GetMapping(path = {""}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getAddOnTemplateItem(HttpServletRequest request,
            @RequestParam(required = false) String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            Page<AddOnTemplateItem> showData = addOnTemplateItemService.getQueryAddonTemplateItem(page,pageSize,groupId);
                    
            response.setStatus(HttpStatus.OK);
            response.setData(showData);
            return ResponseEntity.status(response.getStatus()).body(response);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }


    }

    @PostMapping(path = {""}, name = "stores-post")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postAddOnGroupTemplateItem(
        HttpServletRequest request,
        @Valid @RequestBody AddOnTemplateItemRequest bodyAddOnTemplateItem
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            AddOnTemplateItem body = AddOnTemplateItem.castReference(bodyAddOnTemplateItem);
   
            AddOnTemplateItem data = addOnTemplateItemService.createData(body);

            response.setStatus(HttpStatus.CREATED);
            response.setData(data);
            return ResponseEntity.status(response.getStatus()).body(response);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }
    }


    @PostMapping(path = {"/bulk"}, name = "stores-post")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postBulkAddOnGroupTemplateItem(
        HttpServletRequest request,
        @Valid @RequestBody AddOnTemplateItemRequest[] bodyAddOnTemplateItem
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            // AddOnTemplateItem[] responseData = new AddOnTemplateItem[bodyAddOnTemplateItem.length];
            List<AddOnTemplateItem> responseData = new ArrayList<>();

            for (int i=0;i<bodyAddOnTemplateItem.length;i++) {

                AddOnTemplateItem body = AddOnTemplateItem.castReference(bodyAddOnTemplateItem[i]);

                AddOnTemplateItem data;
                
                if(body.getId()==null){
                    
                    //if id null then create
                    data = addOnTemplateItemService.createData(body);

                }else{
                    
                    //else update
                    data = addOnTemplateItemService.updateAddOnTemplateItem(body.getId(),body);
                }
             
                responseData.add(data);
            }

            response.setStatus(HttpStatus.OK);
            response.setData(responseData);
            return ResponseEntity.status(response.getStatus()).body(response);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }
    }

    @PutMapping(path = {"/{id}"}, name = "store-product-assets-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-product-assets-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putAddOnTemplateItem(
        HttpServletRequest request,
        @PathVariable String id,
        @Valid @RequestBody AddOnTemplateItemRequest bodyAddOnTemplateItem
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");
            
            bodyAddOnTemplateItem.setId(id);

            //set cast reference before proceeding to update
            AddOnTemplateItem body = AddOnTemplateItem.castReference(bodyAddOnTemplateItem);
            
            AddOnTemplateItem data = addOnTemplateItemService.updateAddOnTemplateItem(id,body);

            response.setStatus(HttpStatus.OK);
            response.setData(data);
            return ResponseEntity.status(response.getStatus()).body(response);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-categories-delete-by-id")
    @PreAuthorize("hasAnyAuthority('store-categories-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteAddOnTemplateItem(
        HttpServletRequest request, 
        @PathVariable String id
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());

        Boolean isDeleted = addOnTemplateItemService.deleteAddOnTemplateItem(id);
        HttpStatus httpStatus;

        String message;
        httpStatus = isDeleted ? HttpStatus.OK :HttpStatus.NOT_FOUND;
        message = isDeleted ? "Success Deleted" : "Id Not Found";
        response.setStatus(httpStatus);
        response.setMessage(message);

        return ResponseEntity.status(response.getStatus()).body(response);


    }

    @GetMapping(path = {"{id}"}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getByIdAddOnTemplateItem(    
    HttpServletRequest request, 
    @PathVariable String id
    ){
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {

            Optional<AddOnTemplateItem> data = addOnTemplateItemService.getById(id);
            if(data.isPresent()){
                response.setStatus(HttpStatus.OK);
                response.setData(data.get());
                return ResponseEntity.status(response.getStatus()).body(response);            
            }
            else{
                response.setStatus(HttpStatus.OK);
                response.setMessage("Data not found");
                return ResponseEntity.status(response.getStatus()).body(response);   
            }
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }
    }
}
