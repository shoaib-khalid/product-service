package com.kalsym.product.service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kalsym.product.service.enums.TemplateGroupAndTemplateItemType;
import com.kalsym.product.service.model.product.AddOnTemplateItem;
import com.kalsym.product.service.repository.AddOnTemplateItemRepository;

@Service
public class AddOnTemplateItemService {
    
    @Autowired
    AddOnTemplateItemRepository addOnTemplateItemRepository;

    public Page<AddOnTemplateItem> getQueryAddonTemplateItem(int page, int pageSize, String groupId){

        Pageable pageable = PageRequest.of(page, pageSize);

        AddOnTemplateItem templateItemMatch = new AddOnTemplateItem();
        templateItemMatch.setGroupId(groupId);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<AddOnTemplateItem> templateItemExample = Example.of(templateItemMatch, matcher);

        Page<AddOnTemplateItem> templateItemWithPage = addOnTemplateItemRepository.findAll(templateItemExample, pageable);

        return templateItemWithPage;
    }

    public AddOnTemplateItem createData(AddOnTemplateItem addOnTemplateItem){

        return addOnTemplateItemRepository.save(addOnTemplateItem);

    }

    public Optional<AddOnTemplateItem> getById(String id){

        Optional<AddOnTemplateItem> optTemplateItem = addOnTemplateItemRepository.findById(id);

        return optTemplateItem;
    }

    public Boolean deleteAddOnTemplateItem(String id){
        
        Optional<AddOnTemplateItem> optTemplateItem = addOnTemplateItemRepository.findById(id);

        if (optTemplateItem.isPresent()){
            addOnTemplateItemRepository.deleteById(id);
            return true;

        } else{
            return false;
        }
    }

    public AddOnTemplateItem updateAddOnTemplateItem(String id, AddOnTemplateItem addOnTemplateItem){

        AddOnTemplateItem data = addOnTemplateItemRepository.findById(id).get();
        data.setDineInPrice(addOnTemplateItem.getDineInPrice());
        data.setPrice(addOnTemplateItem.getPrice());
        data.setGroupId(addOnTemplateItem.getGroupId());
        data.setName(addOnTemplateItem.getName());
        
        return addOnTemplateItemRepository.save(data);                                
    }

    public AddOnTemplateItem updateStatusAddOnTemplateItem(String id){
        
        AddOnTemplateItem data = addOnTemplateItemRepository.findById(id).get();
        data.setStatus(TemplateGroupAndTemplateItemType.DELETED.name());
        
        return addOnTemplateItemRepository.save(data);                                
    }
}
