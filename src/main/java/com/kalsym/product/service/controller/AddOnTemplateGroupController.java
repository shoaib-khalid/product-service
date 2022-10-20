package com.kalsym.product.service.controller;

import java.util.List;
import java.util.Optional;

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
import com.kalsym.product.service.model.product.AddOnTemplateGroup;
import com.kalsym.product.service.model.product.AddOnTemplateItem;
import com.kalsym.product.service.model.product.ProductAddOn;
import com.kalsym.product.service.model.request.AddOnGroupTemplateRequest;
import com.kalsym.product.service.repository.AddOnTemplateGroupRepository;
import com.kalsym.product.service.service.AddOnTemplateGroupService;
import com.kalsym.product.service.service.AddOnTemplateItemService;
import com.kalsym.product.service.service.ProductAddOnService;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;

@RestController()
@RequestMapping("/addon-template-group")
public class AddOnTemplateGroupController {

    @Autowired
    AddOnTemplateGroupService addOnTemplateGroupService;

    @Autowired
    ProductAddOnService productAddOnService;

    @Autowired
    AddOnTemplateItemService addOnTemplateItemService;
    
    @GetMapping(path = {""}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getAddOnTemplateGroup(HttpServletRequest request,
            @RequestParam(required = false) String storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            Page<AddOnTemplateGroup> showData = addOnTemplateGroupService.getQueryAddonTemplateGroup(page,pageSize,storeId);
                    
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
        @Valid @RequestBody AddOnGroupTemplateRequest bodyAddOnTemplateGroup
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

            AddOnTemplateGroup body = AddOnTemplateGroup.castReference(bodyAddOnTemplateGroup);
   
            AddOnTemplateGroup data = addOnTemplateGroupService.createData(body);

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
    public ResponseEntity<HttpResponse> putAddOnTemplateGroup(
        HttpServletRequest request,
        @PathVariable String id,
        @Valid @RequestBody AddOnGroupTemplateRequest bodyAddOnTemplateGroup
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");
            
            bodyAddOnTemplateGroup.setId(id);

            //set cast reference before proceeding to update
            AddOnTemplateGroup body = AddOnTemplateGroup.castReference(bodyAddOnTemplateGroup);
            
            AddOnTemplateGroup data = addOnTemplateGroupService.updateAddOnTemplateGroup(id,body);

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
    public ResponseEntity<HttpResponse> deleteAddOnTemplateGroup(
        HttpServletRequest request, 
        @PathVariable String id
    ){

        HttpResponse response = new HttpResponse(request.getRequestURI());
  
        try {

            
            List<ProductAddOn> existingProductAddonTemplateGroupId = productAddOnService.getAllProductAddOnAndStatusNot(id,"DELETED");

            //if there is data we cannot simply change the status we will throw error code
            if(existingProductAddonTemplateGroupId.size()>0){

                response.setStatus(HttpStatus.CONFLICT);
                response.setError(Integer.toString(HttpStatus.CONFLICT.value()));
                response.setMessage("The add on is in used.");
                return ResponseEntity.status(response.getStatus()).body(response);

            }
            else{

                //update status deleted in template group
                AddOnTemplateGroup data = addOnTemplateGroupService.updateStatusAddOnTemplateGroup(id);

                //then we update staus to delete in item template
                List<AddOnTemplateItem> existingAddOnTemplateItemList = addOnTemplateItemService.showAddonTemplateByGroupId(id);

                for(AddOnTemplateItem tempItem:existingAddOnTemplateItemList){

                    addOnTemplateItemService.updateStatusAddOnTemplateItem(tempItem.getId());

                }

                response.setStatus(HttpStatus.OK);
                response.setMessage("Successfully Deleted");
                // response.setData(data);
                return ResponseEntity.status(response.getStatus()).body(response);

            }

         
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }


    }

    @GetMapping(path = {"{id}"}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getByIdAddOnTemplateGroup(    
    HttpServletRequest request, 
    @PathVariable String id
    ){
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {

            Optional<AddOnTemplateGroup> data = addOnTemplateGroupService.getById(id);
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
