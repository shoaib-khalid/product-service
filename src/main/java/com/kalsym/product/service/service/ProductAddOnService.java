package com.kalsym.product.service.service;

import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.product.ProductAddOn;
import com.kalsym.product.service.model.product.ProductAddOnGroup;
import com.kalsym.product.service.model.product.ProductAddOnGroupDetails;
import com.kalsym.product.service.model.product.ProductAddOnItemDetails;
import com.kalsym.product.service.repository.ProductAddOnGroupDetailsRepository;
import com.kalsym.product.service.repository.ProductAddOnGroupRepository;
import com.kalsym.product.service.repository.ProductAddOnRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    ProductAddOnGroupRepository productAddOnGroupRepository;

    public Page<ProductAddOn> getQueryProductAddOn(int page, int pageSize, String addOnItemId){

        Pageable pageable = PageRequest.of(page, pageSize);

        ProductAddOn productAddOnMatch = new ProductAddOn();
        productAddOnMatch.setAddonTemplateItemId(addOnItemId);

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

        return productAddOnRepository.save(data.updateData(data, productAddOn));                                
    }

    public List<ProductAddOn> getAllProductByProductId(String productId){

        List<ProductAddOn> getData = productAddOnRepository.findByProductIdAndStatusNot(productId,"DELETED");
        
        return getData;
    }

    public List<ProductAddOn> getByProductAddonGroupId(String productAddonGroupId){

        List<ProductAddOn> getData = productAddOnRepository.findByProductAddonGroupIdAndStatusNot(productAddonGroupId,"DELETED");
        
        return getData;
    }

    public List<ProductAddOnGroupDetails> transformDataGroupTemplateofProductAddOn(List<ProductAddOn> showData, String productId){

        //extract details of product add on first
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

            //aka templategroupid
            productAddOnItemDetails.setGroupId(mapper.getProductAddOnItemDetails().getGroupId());

            productAddOnItemDetails.setAddonTemplateItemId(mapper.getAddonTemplateItemId());
            productAddOnItemDetails.setSequenceNumber(mapper.getSequenceNumber());
            productAddOnItemDetails.setProductAddonGroupId(mapper.getProductAddonGroupId());

            return productAddOnItemDetails;
        })
        .collect(Collectors.toList());

        //extract info on addon_template_group  
        List<ProductAddOnGroupDetails> result2 = showData.stream()
        .map(mapper->{
            ProductAddOnGroupDetails productAddOnGroupDetails = mapper.getProductAddOnItemDetails().getProductAddOnGroupDetails();
            return productAddOnGroupDetails;
        })
        .distinct()
        .collect(Collectors.toList());

        //merge the info into one collection
        List<ProductAddOnGroupDetails> result3 = result2.stream()
        .map(mapper->{

            List<ProductAddOnItemDetails> filterByGroupId =result.stream()
            .filter(x -> x.getGroupId().equals(mapper.getId()))
            .collect(Collectors.toList());
            //get product add on group details 
            ProductAddOnGroup productAddOnGroup = productAddOnGroupRepository.findByProductIdAndAddonTemplateGroupIdAndStatusNot(productId,mapper.getId(),"DELETED").get();
            ProductAddOnGroupDetails productAddOnGroupDetails = mapper;
            productAddOnGroupDetails.setGroupId(productAddOnGroup.getAddonTemplateGroupId());
            productAddOnGroupDetails.setProductAddOnItemDetail(filterByGroupId);
            productAddOnGroupDetails.setMaxAllowed(productAddOnGroup.getMaxAllowed());
            productAddOnGroupDetails.setMinAllowed(productAddOnGroup.getMinAllowed());
            productAddOnGroupDetails.setSequenceNumber(productAddOnGroup.getSequenceNumber());
            productAddOnGroupDetails.setId(productAddOnGroup.getId());
            return productAddOnGroupDetails;
        })
        .collect(Collectors.toList());

        //nested sort
        List<ProductAddOnGroupDetails> sortedList = result3.stream()
        .sorted(Comparator.comparingInt(ProductAddOnGroupDetails::getSequenceNumber))
        .map(mapper -> {
            List<ProductAddOnItemDetails> packageOptDetails = mapper.getProductAddOnItemDetail().stream()
            .sorted(Comparator.comparingInt(ProductAddOnItemDetails::getSequenceNumber))
            .collect(Collectors.toList());
            mapper.setProductAddOnItemDetail(packageOptDetails);
            return mapper;
        })
        .collect(Collectors.toList());
        
        return sortedList;
    }

    // public List<ProductAddOnGroupDetails> getAllProductAddOnGroupDetails(String productId){

    //     ProductAddOnGroupDetails productMatch = new ProductAddOnGroupDetails();

    //     ExampleMatcher matcher = ExampleMatcher
    //     .matchingAll()
    //     .withIgnoreCase()
    //     .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
    //     Example<ProductAddOnGroupDetails> example = Example.of(productMatch, matcher);

    //     Specification<ProductAddOnGroupDetails> productSpecs = searchProductAddOnSpecs(productId,example);

    //     List<ProductAddOnGroupDetails> data = productAddOnGroupDetailsRepository.findAll(productSpecs);

    //     // List<ProductAddOnGroupDetails> data = productAddOnGroupDetailsRepository.getProductAddOnJpqlQuery(productId);

    //     return data ;
    // }

    // public static Specification<ProductAddOnGroupDetails> searchProductAddOnSpecs(
    //     String productId, 
    //     Example<ProductAddOnGroupDetails> example) {

    //     return (Specification<ProductAddOnGroupDetails>) (root, query, builder) -> {
    //         final List<Predicate> predicates = new ArrayList<>();

    //         Join<ProductAddOnGroupDetails, ProductAddOnItemDetails> addOnItemDetails = root.join("productAddOnItemDetails");
    //         Join<ProductAddOnItemDetails, ProductAddOn> addOnDetails = addOnItemDetails.join("productAddOnDetails", JoinType.INNER);
    //         if (productId != null && !productId.isEmpty()) {
    //             predicates.add(builder.equal(addOnDetails.get("productId"), productId));
    //         }

    //         predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));
    //         query.distinct(true);

    //         return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    //     };
    // }
}
