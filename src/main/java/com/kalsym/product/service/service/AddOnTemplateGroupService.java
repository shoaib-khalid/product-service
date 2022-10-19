package com.kalsym.product.service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kalsym.product.service.enums.TemplateGroupAndTemplateItemType;
import com.kalsym.product.service.model.product.AddOnTemplateGroup;
import com.kalsym.product.service.model.product.ProductAddOn;
import com.kalsym.product.service.repository.AddOnTemplateGroupRepository;

@Service
public class AddOnTemplateGroupService {
    
    @Autowired
    AddOnTemplateGroupRepository addOnTemplateGroupRepository;

    public Page<AddOnTemplateGroup> getQueryAddonTemplateGroup(int page, int pageSize, String storeId){

        Pageable pageable = PageRequest.of(page, pageSize);

        AddOnTemplateGroup templateGroupMatch = new AddOnTemplateGroup();

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<AddOnTemplateGroup> templateGroupExample = Example.of(templateGroupMatch, matcher);

        Specification<AddOnTemplateGroup> addOnTemplateGroupSpecs = searchAddOnTemplateGroupSpecs(storeId, TemplateGroupAndTemplateItemType.DELETED,templateGroupExample);

        Page<AddOnTemplateGroup> templateGroupWithPage = addOnTemplateGroupRepository.findAll(addOnTemplateGroupSpecs, pageable);

        return templateGroupWithPage;
    }

    public AddOnTemplateGroup createData(AddOnTemplateGroup addOnTemplateGroup){

        addOnTemplateGroup.setStatus(TemplateGroupAndTemplateItemType.AVAILABLE);
        
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

    public AddOnTemplateGroup updateStatusAddOnTemplateGroup(String id){
        
        AddOnTemplateGroup data = addOnTemplateGroupRepository.findById(id).get();
        data.setStatus(TemplateGroupAndTemplateItemType.DELETED);
        
        return addOnTemplateGroupRepository.save(data);                                
    }

    public static Specification<AddOnTemplateGroup> searchAddOnTemplateGroupSpecs(
        String storeId, 
        TemplateGroupAndTemplateItemType status, 
        Example<AddOnTemplateGroup> example) {

        return (Specification<AddOnTemplateGroup>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (storeId != null && !storeId.isEmpty()) {
                predicates.add(builder.equal(root.get("storeId"), storeId));
            }

            if (status != null) {
                predicates.add(builder.notEqual(root.get("status"), status));
            }

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
 
}
