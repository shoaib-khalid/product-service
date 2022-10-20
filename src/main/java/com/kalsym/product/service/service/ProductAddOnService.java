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

    public Page<ProductAddOn> getQueryProductAddOn(int page, int pageSize,String addonTemplateGroupId,String status){

        Pageable pageable = PageRequest.of(page, pageSize);

        ProductAddOn productAddOnMatch = new ProductAddOn();

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductAddOn> productAddOnExample = Example.of(productAddOnMatch, matcher);

        Specification<ProductAddOn> productAddOnSpecs = searchProductAddOnSpecs(status,addonTemplateGroupId,productAddOnExample);

        Page<ProductAddOn> productAddOnWithPage = productAddOnRepository.findAll(productAddOnSpecs, pageable);

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

    public List<ProductAddOn> getAllProductAddOnAndStatusNot(String addonTemplateGroupId,String status){

        //find with limit 
        Page<ProductAddOn> productAddOnWithPage = getQueryProductAddOn(0,5,addonTemplateGroupId,status);

        List<ProductAddOn> getData =productAddOnWithPage.getContent();
        
        return getData;
    }

    public List<ProductAddOn> findTop5ByAddonTemplateItemIdAndStatusNot(String templateItemid,String status){

        //find with limit 
        List<ProductAddOn> getData =productAddOnRepository.findTop5ByAddonTemplateItemIdAndStatusNot(templateItemid, status);
        
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
        // System.out.println("ProductAddOnItemDetails::::::::::::::"+result);



        //extract info on addon_template_group  
        List<ProductAddOnGroupDetails> result2 = showData.stream()
        .map(mapper->{
            ProductAddOnGroupDetails productAddOnGroupDetails = mapper.getProductAddOnItemDetails().getProductAddOnGroupDetails();
            productAddOnGroupDetails.setGroupId(productId);
            return productAddOnGroupDetails;
        })
        .distinct()
        .collect(Collectors.toList());
        // System.out.println("CHECKING DISTINCT OF GROUP"+result2);

        //merge the info into one collection
        List<ProductAddOnGroupDetails> result3 = result2.stream()
        .map(mapper->{

            List<ProductAddOnItemDetails> filterByGroupId =result.stream()
            .filter(x -> x.getGroupId().equals(mapper.getId()))
            .collect(Collectors.toList());
            //get product add on group details 
            // ProductAddOnGroup productAddOnGroup = productAddOnGroupRepository.findByProductIdAndAddonTemplateGroupIdAndStatusNot(productId,mapper.getId(),"DELETED").get();
            ProductAddOnGroup productAddOnGroup = productAddOnGroupRepository.findByIdAndProductIdAndAddonTemplateGroupIdAndStatusNot(filterByGroupId.get(0).getProductAddonGroupId(),productId,mapper.getId(),"DELETED").get();

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

    public static Specification<ProductAddOn> searchProductAddOnSpecs(
        String status, 
        String productAddonTemplateGroupId,
        Example<ProductAddOn> example) {

        return (Specification<ProductAddOn>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<ProductAddOn, ProductAddOnItemDetails> productAddOnItemDetails = root.join("productAddOnItemDetails");
            Join<ProductAddOnItemDetails, ProductAddOnGroupDetails> productAddOnGroupDetails = productAddOnItemDetails.join("productAddOnGroupDetails");


            if (status != null) {
                predicates.add(builder.notEqual(root.get("status"), status));
            }

            if (productAddonTemplateGroupId != null) {
                predicates.add(builder.equal(productAddOnGroupDetails.get("id"), productAddonTemplateGroupId));
            }

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
