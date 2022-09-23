package com.kalsym.product.service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.product.AddOnTemplateGroup;
import com.kalsym.product.service.repository.AddOnTemplateGroupRepository;

@Service
public class AddOnTemplateGroupService {
    
    @Autowired
    AddOnTemplateGroupRepository addOnTemplateGroupRepository;

    public Page<AddOnTemplateGroup> getQueryAddonTemplateGroup(int page, int pageSize, String storeId){

        Pageable pageable = PageRequest.of(page, pageSize);

        AddOnTemplateGroup templateGroupMatch = new AddOnTemplateGroup();
        templateGroupMatch.setStoreId(storeId);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<AddOnTemplateGroup> templateGroupExample = Example.of(templateGroupMatch, matcher);

        Page<AddOnTemplateGroup> templateGroupWithPage = addOnTemplateGroupRepository.findAll(templateGroupExample, pageable);

        return templateGroupWithPage;
    }

    public AddOnTemplateGroup createData(AddOnTemplateGroup addOnTemplateGroup){

        return addOnTemplateGroupRepository.save(addOnTemplateGroup);

    }

    public Optional<AddOnTemplateGroup> getById(String id){

        Optional<AddOnTemplateGroup> optTemplateGroup = addOnTemplateGroupRepository.findById(id);

        return optTemplateGroup;
    }

    public Boolean deleteAddOnTemplateGroup(String id){
        
        Optional<AddOnTemplateGroup> optTemplateGroup = addOnTemplateGroupRepository.findById(id);

        if (optTemplateGroup.isPresent()){
            addOnTemplateGroupRepository.deleteById(id);
            return true;

        } else{
            return false;
        }
    }

    public AddOnTemplateGroup updateAddOnTemplateGroup(String id, AddOnTemplateGroup addOnTemplateGroup){

        AddOnTemplateGroup data = addOnTemplateGroupRepository.findById(id).get();
        data.setTitle(addOnTemplateGroup.getTitle());
        
        return addOnTemplateGroupRepository.save(data);                                
    }
 
}
