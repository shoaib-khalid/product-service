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
import com.kalsym.product.service.model.product.ProductAddOnGroupDetails;
import com.kalsym.product.service.model.product.ProductAddOnItemDetails;
import com.kalsym.product.service.model.request.ProductAddOnRequest;
import com.kalsym.product.service.service.ProductAddOnService;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;


@RestController()
@RequestMapping("/product-addon")
public class ProductAddOnController {
    
    @Autowired
    ProductAddOnService productAddOnService;
    
    // @GetMapping(path = {""}, name = "store-categories-get", produces = "application/json")
    // @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    // public ResponseEntity<HttpResponse> getProductAddOn(HttpServletRequest request,
    //         @RequestParam(required = false) String addOnItemId,
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "20") int pageSize) {

    //     String logprefix = request.getRequestURI();
    //     HttpResponse response = new HttpResponse(request.getRequestURI());

    //     try {
    //         Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

    //         Page<ProductAddOn> showData = productAddOnService.getQueryProductAddOn(page,pageSize,addOnItemId);
                    
    //         response.setStatus(HttpStatus.OK);
    //         response.setData(showData);
    //         return ResponseEntity.status(response.getStatus()).body(response);
            
    //     } catch (Exception e) {
    //         // TODO: handle exception
    //         response.setStatus(HttpStatus.BAD_REQUEST);
    //         response.setError(e.toString());
    //         return ResponseEntity.status(response.getStatus()).body(response);
    //     }


    // }

    @GetMapping(path = {""}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getProductAddOn(HttpServletRequest request,
            @RequestParam(required = true) String productId) {

        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            List<ProductAddOn> showData = productAddOnService.getAllProductByProductId(productId);
            // List<ProductAddOnGroupDetails> showData = productAddOnService.getAllProductAddOnGroupDetails(productId);

            //group by addontemplategroupid
            List<ProductAddOnItemDetails> result = 
			showData.stream()
            .map(mapper->{
                // ProductAddOnItemDetails productAddOnItemDetails = mapper.getProductAddOnItemDetails();
                ProductAddOnItemDetails productAddOnItemDetails = new ProductAddOnItemDetails();
                productAddOnItemDetails.setId(mapper.getId());
                productAddOnItemDetails.setProductId(mapper.getProductId());
                productAddOnItemDetails.setPrice(mapper.getPrice());
                productAddOnItemDetails.setDineInPrice(mapper.getDineInPrice());
                productAddOnItemDetails.setStatus(mapper.getStatus());
                productAddOnItemDetails.setName(mapper.getProductAddOnItemDetails().getName());
                productAddOnItemDetails.setGroupId(mapper.getProductAddOnItemDetails().getGroupId());

                return productAddOnItemDetails;
            })
            .collect(Collectors.toList());

            List<ProductAddOnGroupDetails> result2 = showData.stream()
            .map(mapper->{
                ProductAddOnGroupDetails productAddOnGroupDetails = mapper.getProductAddOnItemDetails().getProductAddOnGroupDetails();
                return productAddOnGroupDetails;
            })
            .distinct()
            .collect(Collectors.toList());

            List<ProductAddOnGroupDetails> result3 = result2.stream()
            .map(mapper->{

                List<ProductAddOnItemDetails> filterByGroupId =result.stream()
                .filter(x -> x.getGroupId().equals(mapper.getId()))
                .collect(Collectors.toList());
                System.out.println("CHECKING DALAMA JEE:::"+filterByGroupId);
                ProductAddOnGroupDetails productAddOnGroupDetails = mapper;
                productAddOnGroupDetails.setProductAddOnItemDetail(filterByGroupId);
                return productAddOnGroupDetails;
            })
            .collect(Collectors.toList());
            

            System.out.println("CHECKINGGGGGresult3 :::"+result3);

                    
            response.setStatus(HttpStatus.OK);
            response.setData(result3);
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
        @Valid @RequestBody ProductAddOnRequest bodyProductAddOn
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            ProductAddOn body = ProductAddOn.castReference(bodyProductAddOn);
   
            ProductAddOn data = productAddOnService.createData(body);

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
        @Valid @RequestBody ProductAddOnRequest bodyProductAddOn
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");
            
            bodyProductAddOn.setId(id);

            //set cast reference before proceeding to update
            ProductAddOn body = ProductAddOn.castReference(bodyProductAddOn);
            
            ProductAddOn data = productAddOnService.updateProductAddOn(id,body);

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

        Boolean isDeleted = productAddOnService.deleteProductAddOn(id);
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
    public ResponseEntity<HttpResponse> getByIdProductAddOn(    
    HttpServletRequest request, 
    @PathVariable String id
    ){
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {

            Optional<ProductAddOn> data = productAddOnService.getById(id);
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
