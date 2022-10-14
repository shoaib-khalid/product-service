package com.kalsym.product.service.controller;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import com.kalsym.product.service.model.product.ProductAddOn;
import com.kalsym.product.service.model.product.ProductAddOnGroup;
import com.kalsym.product.service.model.product.ProductAddOnGroupDetails;
import com.kalsym.product.service.model.product.ProductAddOnItemDetails;
import com.kalsym.product.service.model.request.ProductAddOnRequest;
import com.kalsym.product.service.model.request.ProductAddonGroupRequest;
import com.kalsym.product.service.service.ProductAddOnGroupService;
import com.kalsym.product.service.service.ProductAddOnService;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;


@RestController()
@RequestMapping("/product-addon-group")
public class ProductAddonGroupController {

    @Autowired
    ProductAddOnGroupService productAddOnGroupService;

    @Autowired
    ProductAddOnService productAddOnService;
    
    @GetMapping(path = {""}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getProductAddOn(HttpServletRequest request,
            @RequestParam(required = true) String productId) {

        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            List<ProductAddOnGroup> showData = productAddOnGroupService.listOfProductAddsOnGroup(productId);
            // List<ProductAddOnGroupDetails> showData = productAddOnService.getAllProductAddOnGroupDetails(productId);

            // List<ProductAddOnGroupDetails> resultData = productAddOnGroupService.transformDataGroupTemplateofProductAddOn(showData);
        
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
    public ResponseEntity<HttpResponse> postAddOnGroupTemplate(
        HttpServletRequest request,
        @Valid @RequestBody ProductAddonGroupRequest bodyProductAddonGroup
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            ProductAddOnGroup body = ProductAddOnGroup.castReference(bodyProductAddonGroup);
   
            ProductAddOnGroup data = productAddOnGroupService.createProductAddonGroup(body);

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


    @PutMapping(path = {"/{id}"}, name = "store-product-assets-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-product-assets-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putProductAddOn(
        HttpServletRequest request,
        @PathVariable String id,
        @Valid @RequestBody ProductAddonGroupRequest bodyProductAddonGroup
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");
            
            bodyProductAddonGroup.setId(id);

            //set cast reference before proceeding to update
            ProductAddOnGroup body = ProductAddOnGroup.castReference(bodyProductAddonGroup);
            
            ProductAddOnGroup data = productAddOnGroupService.updateProductAddsOnGroup(id,body);

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
    public ResponseEntity<HttpResponse> deleteProductAddOn(
        HttpServletRequest request, 
        @PathVariable String id
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());

        //uPDATE THE DATA STATUS TO DELETED
        Optional<ProductAddOnGroup> optData = productAddOnGroupService.getById(id);
        ProductAddOnGroup body = optData.get();
        
        if(!optData.isPresent()){

            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError(Integer.toString(HttpStatus.NOT_FOUND.value()));
            return ResponseEntity.status(response.getStatus()).body(response);    
        }

        //update the data set the status to "DELETED"
        body.setStatus("DELETED");
        ProductAddOnGroup data = productAddOnGroupService.updateProductAddsOnGroup(id,body);

        //THEN FIND ANY OF PRODUCT ADD ON WHICH LINK TO PRODUCTADDONGROUPID
        List<ProductAddOn> getListOfProductAddon = productAddOnService.getByProductAddonGroupId(id);

        if(getListOfProductAddon.size()>0){

            for (int i=0;i<getListOfProductAddon.size();i++) {

                //set the status the update
                ProductAddOn productAddonData = getListOfProductAddon.get(i);
                productAddonData.setStatus("DELETED");

                productAddOnService.updateProductAddOn(productAddonData.getId(), productAddonData);

            }
        }

        response.setStatus(HttpStatus.OK);
        response.setData(data);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping(path = {"{id}"}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getByIdProductAddOn(    
    HttpServletRequest request, 
    @PathVariable String id
    ){
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {

            Optional<ProductAddOnGroup> data = productAddOnGroupService.getById(id);
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
