package com.kalsym.product.service.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalsym.product.service.model.product.AddOnTemplateGroup;
import com.kalsym.product.service.model.product.AddOnTemplateItem;
import com.kalsym.product.service.model.product.ProductAddOn;
import com.kalsym.product.service.model.product.ProductAddOnGroup;
import com.kalsym.product.service.model.store.CompareStoreTemplateGroup;
import com.kalsym.product.service.model.store.CompareStoreTemplateItem;
import com.kalsym.product.service.repository.AddOnTemplateGroupRepository;
import com.kalsym.product.service.repository.AddOnTemplateItemRepository;
import com.kalsym.product.service.repository.ProductAddOnGroupRepository;
import com.kalsym.product.service.repository.ProductAddOnRepository;
import com.kalsym.product.service.utility.HttpResponse;

@RestController()
@RequestMapping("/code")
public class CodePlaygroundController {
    
    @Autowired
    AddOnTemplateGroupRepository addOnTemplateGroupRepository;

    @Autowired
    AddOnTemplateItemRepository addOnTemplateItemRepository;

    @Autowired 
    ProductAddOnRepository productAddOnRepository;

    @Autowired 
    ProductAddOnGroupRepository productAddOnGroupRepository;

    @GetMapping(path = {""}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getTemplateGroup(HttpServletRequest request,
            @RequestParam(required = false) String storeId) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {

            //get all template group  with template item  based on store owner id
            List<AddOnTemplateGroup> storeOwnerTemplateGroup = addOnTemplateGroupRepository.findByStoreIdAndStatusNot(storeId, "DELETED");
            List<AddOnTemplateGroup> mapNewTemplateGroup = new ArrayList<>();

            //compare data (Template Group)
            List<CompareStoreTemplateGroup> compareStoreOwnerTemplateGroup = new ArrayList<>();

            // List<CompareStoreTemplateItem> compareStoreOwnerTemplateItem = new ArrayList<>();

            List<CompareStoreTemplateGroup> filterData ;

            mapNewTemplateGroup = storeOwnerTemplateGroup.stream()
            .map(addontemplategroup->{
    
                AddOnTemplateGroup bodyAddOnTemplateGroup = new AddOnTemplateGroup();
                bodyAddOnTemplateGroup.setTitle(addontemplategroup.getTitle());
                bodyAddOnTemplateGroup.setStoreId(storeId);
                bodyAddOnTemplateGroup.setStatus(addontemplategroup.getStatus());
                //saving the data for branch
                //AddOnTemplateGroup saveAddOnTemplateGroup = addOnTemplateGroupRepository.save(bodyAddOnTemplateGroup);

                //get template items and save em
                List<AddOnTemplateItem> templateItemStoreOwner  = addontemplategroup.getAddOnTemplateItem();

                List<CompareStoreTemplateItem> compareStoreOwnerTemplateItem = new ArrayList<>();
                
                System.out.println("CHECKING DATA :::::"+templateItemStoreOwner);

                //set data for comparing purose
                CompareStoreTemplateGroup compareData = new CompareStoreTemplateGroup();
                compareData.setStoreTemplateGroupId(addontemplategroup.getId());
                compareData.setTitle(addontemplategroup.getTitle());

                //comment this
                compareData.setBranchTemplateGroupId(addontemplategroup.getId());

                templateItemStoreOwner.stream()
                .map(addontemplateitem->{

                    AddOnTemplateItem bodyAddonTemplateItem = new AddOnTemplateItem();
                    bodyAddonTemplateItem.setStatus(storeId);
                    bodyAddonTemplateItem.setGroupId(addontemplategroup.getId());
                    bodyAddonTemplateItem.setName(addontemplateitem.getName());
                    bodyAddonTemplateItem.setPrice(addontemplateitem.getPrice());
                    bodyAddonTemplateItem.setDineInPrice(addontemplateitem.getDineInPrice());

                    //saving the data for branch
                    //AddOnTemplateItem saveAddOnTemplateItem = addOnTemplateItemRepository.save(bodyAddonTemplateItem);

                    CompareStoreTemplateItem compareDataTemplateItem = new CompareStoreTemplateItem();
                    compareDataTemplateItem.setStoreTemplateItem(addontemplateitem.getId());
                    compareDataTemplateItem.setBranchTemplateItem(addontemplateitem.getId());
                    compareDataTemplateItem.setName(addontemplateitem.getName());

                    compareStoreOwnerTemplateItem.add(compareDataTemplateItem);

                    return addontemplateitem;
                })
                .collect(Collectors.toList());

                compareData.setCompareTemplateItem(compareStoreOwnerTemplateItem);

                compareStoreOwnerTemplateGroup.add(compareData);
    
                return bodyAddOnTemplateGroup;
    
            })
            .collect(Collectors.toList());
    
            filterData = compareStoreOwnerTemplateGroup.stream()
            .filter(mapper -> mapper.getCompareTemplateItem()
                            .stream()
                            .anyMatch(b-> b.getName().equals("Medium")
            ))
            .map(templategroup -> {

                List<CompareStoreTemplateItem> templateItemDetails = templategroup.getCompareTemplateItem()
                .stream()
                .sorted(
                    Comparator.comparing((CompareStoreTemplateItem t) -> !t.getName().equals("Medium"))
                    .thenComparing(CompareStoreTemplateItem::getName)
                )
                .collect(Collectors.toList());

                templategroup.setCompareTemplateItem(templateItemDetails);
    
                return templategroup;
            })
            .collect(Collectors.toList());

            CompareStoreTemplateGroup filterDataTemplateGroup = compareStoreOwnerTemplateGroup.stream()
            .filter(mapper -> mapper.getCompareTemplateItem()
                            .stream()
                            .anyMatch(b-> b.getName().equals("Medium")
            ))
            .map(templategroup -> {

                List<CompareStoreTemplateItem> templateItemDetails = templategroup.getCompareTemplateItem()
                .stream()
                .sorted(
                    Comparator.comparing((CompareStoreTemplateItem t) -> !t.getName().equals("Medium"))
                    .thenComparing(CompareStoreTemplateItem::getName)
                )
                .collect(Collectors.toList());

                templategroup.setCompareTemplateItem(templateItemDetails);
    
                return templategroup;
            })
            .findFirst().get();


            
            response.setStatus(HttpStatus.OK);
            response.setData(filterDataTemplateGroup);
            return ResponseEntity.status(response.getStatus()).body(response);
            
        } catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();            
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }


    }

    @PostMapping(path = {"/try"}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getSelectedProduct(HttpServletRequest request,
            @RequestParam(required = true) String storeOwnerId,
            @RequestParam(required = true) String storeBranchId,
            @RequestBody List<String> productIds
            ) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {

            List<AddOnTemplateGroup> storeOwnerTemplateGroup = addOnTemplateGroupRepository.findByStoreIdAndStatusNot(storeOwnerId, "DELETED");

            List<AddOnTemplateGroup> storeBranchTemplateGroup = addOnTemplateGroupRepository.findByStoreIdAndStatusNot(storeBranchId, "DELETED");

            List<CompareStoreTemplateGroup> compareStoreOwnerTemplateGroup = new ArrayList<>();

            storeOwnerTemplateGroup.stream()
            .map((AddOnTemplateGroup x)->{

                Optional<AddOnTemplateGroup> optFilterStoreBranchAddOnTemplateGroup = storeBranchTemplateGroup.stream()
                .filter((AddOnTemplateGroup atg) -> atg.getTitle().contains(x.getTitle()))
                .findFirst();

                CompareStoreTemplateGroup compareTemplateGroup = new CompareStoreTemplateGroup();
                compareTemplateGroup.setStoreTemplateGroupId(x.getId());
                compareTemplateGroup.setTitle(x.getTitle());
                compareTemplateGroup.setBranchTemplateGroupId(optFilterStoreBranchAddOnTemplateGroup.isPresent()?optFilterStoreBranchAddOnTemplateGroup.get().getId():null);

                List<CompareStoreTemplateItem> compareStoreOwnerTemplateItem = new ArrayList<>();

                x.getAddOnTemplateItem().stream()
                .map( (AddOnTemplateItem ownerTemplateItem)->{

                    CompareStoreTemplateItem compareDataTemplateItem = new CompareStoreTemplateItem();
                    compareDataTemplateItem.setStoreTemplateItem(ownerTemplateItem.getId());
                    compareDataTemplateItem.setName(ownerTemplateItem.getName());


                    if(optFilterStoreBranchAddOnTemplateGroup.isPresent()){
                        Optional<AddOnTemplateItem> optFilterStoreBranchTemplateItem = optFilterStoreBranchAddOnTemplateGroup
                        .get().getAddOnTemplateItem()
                        .stream()
                        .filter((AddOnTemplateItem ati) -> ati.getName().contains(ownerTemplateItem.getName()))
                        .findFirst();

                        compareDataTemplateItem.setBranchTemplateItem(optFilterStoreBranchTemplateItem.isPresent()?optFilterStoreBranchTemplateItem.get().getId():null);


                    } else{
                        compareDataTemplateItem.setBranchTemplateItem(null);
                    }
               

                  
                    compareStoreOwnerTemplateItem.add(compareDataTemplateItem);

                    return ownerTemplateItem;
                })
                .collect(Collectors.toList());

                compareTemplateGroup.setCompareTemplateItem(compareStoreOwnerTemplateItem);

                compareStoreOwnerTemplateGroup.add(compareTemplateGroup);
                
                System.out.println("compareStoreOwnerTemplateGroup jeeeee"+compareStoreOwnerTemplateGroup);

                return x;
            })
            .collect(Collectors.toList());

            response.setStatus(HttpStatus.OK);
            response.setData(compareStoreOwnerTemplateGroup);
            return ResponseEntity.status(response.getStatus()).body(response); 
        } catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();            
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }


    }

}
