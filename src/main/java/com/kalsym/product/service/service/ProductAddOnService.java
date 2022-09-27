package com.kalsym.product.service.service;

import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.product.ProductAddOn;
import com.kalsym.product.service.repository.ProductAddOnRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductAddOnService {
    
    @Autowired 
    ProductAddOnRepository productAddOnRepository;

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
}
