package com.kalsym.product.service.service;

import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.product.ProductAddOn;
import com.kalsym.product.service.model.product.ProductAddOnGroupDetails;
import com.kalsym.product.service.model.product.ProductAddOnItemDetails;
import com.kalsym.product.service.repository.ProductAddOnGroupDetailsRepository;
import com.kalsym.product.service.repository.ProductAddOnRepository;

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
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;


@Service
public class ProductAddOnService {
    
    @Autowired 
    ProductAddOnRepository productAddOnRepository;

    @Autowired
    ProductAddOnGroupDetailsRepository productAddOnGroupDetailsRepository;

    public Page<ProductAddOn> getQueryProductAddOn(int page, int pageSize, String addOnItemId){

        Pageable pageable = PageRequest.of(page, pageSize);

        ProductAddOn productAddOnMatch = new ProductAddOn();
        productAddOnMatch.setAddOnItemId(addOnItemId);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductAddOn> productAddOnExample = Example.of(productAddOnMatch, matcher);

        Page<ProductAddOn> productAddOnWithPage = productAddOnRepository.findAll(productAddOnExample, pageable);

        return productAddOnWithPage;
    }

    public ProductAddOn createData(ProductAddOn ProductAddOn){

        return productAddOnRepository.save(ProductAddOn);

    }

    public Optional<ProductAddOn> getById(String id){

        Optional<ProductAddOn> optTemplateItem = productAddOnRepository.findById(id);

        return optTemplateItem;
    }

    public Boolean deleteProductAddOn(String id){
        
        Optional<ProductAddOn> optTemplateItem = productAddOnRepository.findById(id);

        if (optTemplateItem.isPresent()){
            productAddOnRepository.deleteById(id);
            return true;

        } else{
            return false;
        }
    }

    public ProductAddOn updateProductAddOn(String id, ProductAddOn productAddOn){

        ProductAddOn data = productAddOnRepository.findById(id).get();
        data.setDineInPrice(productAddOn.getDineInPrice());
        data.setPrice(productAddOn.getPrice());
        data.setStatus(productAddOn.getStatus());
        
        return productAddOnRepository.save(data);                                
    }

    public List<ProductAddOn> getAllProductByProductId(String productId){

        List<ProductAddOn> getData = productAddOnRepository.findByProductId(productId);
        return getData;
    }

    public List<ProductAddOnGroupDetails> getAllProductAddOnGroupDetails(String productId){

        ProductAddOnGroupDetails productMatch = new ProductAddOnGroupDetails();

        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductAddOnGroupDetails> example = Example.of(productMatch, matcher);

        Specification<ProductAddOnGroupDetails> productSpecs = searchProductAddOnSpecs(productId,example);

        List<ProductAddOnGroupDetails> data = productAddOnGroupDetailsRepository.findAll(productSpecs);

        // List<ProductAddOnGroupDetails> data = productAddOnGroupDetailsRepository.getProductAddOnJpqlQuery(productId);

        return data ;
    }

    public static Specification<ProductAddOnGroupDetails> searchProductAddOnSpecs(
        String productId, 
        Example<ProductAddOnGroupDetails> example) {

        return (Specification<ProductAddOnGroupDetails>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            Join<ProductAddOnGroupDetails, ProductAddOnItemDetails> addOnItemDetails = root.join("productAddOnItemDetails");
            Join<ProductAddOnItemDetails, ProductAddOn> addOnDetails = addOnItemDetails.join("productAddOnDetails", JoinType.INNER);
            if (productId != null && !productId.isEmpty()) {
                predicates.add(builder.equal(addOnDetails.get("productId"), productId));
            }

            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));
            query.distinct(true);

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
