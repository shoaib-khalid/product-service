package com.kalsym.product.service.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalsym.product.service.model.product.AddOnTemplateGroup;
import com.kalsym.product.service.model.product.AddOnTemplateItem;
import com.kalsym.product.service.model.store.CompareStoreTemplateGroup;
import com.kalsym.product.service.model.store.CompareStoreTemplateItem;
import com.kalsym.product.service.repository.AddOnTemplateGroupRepository;
import com.kalsym.product.service.repository.AddOnTemplateItemRepository;
import com.kalsym.product.service.utility.HttpResponse;

@RestController()
@RequestMapping("/code")
public class CodePlaygroundController {
    
    @Autowired
    AddOnTemplateGroupRepository addOnTemplateGroupRepository;

    @Autowired
    AddOnTemplateItemRepository addOnTemplateItemRepository;

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

}
