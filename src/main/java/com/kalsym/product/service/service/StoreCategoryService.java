package com.kalsym.product.service.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.repository.StoreCategoryRepository;

@Service
public class StoreCategoryService {
    
    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    public Page<StoreCategory> searchWithCriteria(List<String> verticalList, int page,int pageSize){

        StoreCategory categoryMatch = new StoreCategory();
        
        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<StoreCategory> example = Example.of(categoryMatch, matcher);
        
        Pageable pageable = PageRequest.of(page, pageSize);

        Specification<StoreCategory> parentCategorySpecs = searchCategorySpecs(verticalList,example);
        Page<StoreCategory> result = storeCategoryRepository.findAll(parentCategorySpecs, pageable);   

        return result;
    }

    public static Specification<StoreCategory> searchCategorySpecs(
        List<String> verticalList,
        Example<StoreCategory> example
    ){

        return (Specification<StoreCategory>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
 
            // if (categoryId != null) {
            //     predicates.add(builder.equal(root.get("categoryId"), categoryId));
            // }

            if (verticalList!=null) {
                predicates.add(builder.in(root.get("verticalCode")).value(verticalList));
            }

            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
