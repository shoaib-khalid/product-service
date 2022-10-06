package com.kalsym.product.service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.product.ProductAddOnGroup;
import com.kalsym.product.service.repository.ProductAddOnGroupRepository;

@Service
public class ProductAddOnGroupService {

    @Autowired 
    ProductAddOnGroupRepository productAddOnGroupRepository;
   
    public ProductAddOnGroup createProductAddonGroup(ProductAddOnGroup productAddonGroup){
        
        return productAddOnGroupRepository.save(productAddonGroup);

    }

    public List<ProductAddOnGroup> listOfProductAddsOnGroup(String productId){
        
        List<ProductAddOnGroup> getData = productAddOnGroupRepository.findByProductId(productId);
        return getData;

    }

    public ProductAddOnGroup updateProductAddsOnGroup(String id, ProductAddOnGroup productAddonGroup){

        ProductAddOnGroup data = productAddOnGroupRepository.findById(id).get();
        data.setAddonGroupId(productAddonGroup.getAddonGroupId());
        data.setMaxAllowed(productAddonGroup.getMaxAllowed());
        data.setMinAllowed(productAddonGroup.getMinAllowed());
        data.setSequenceNumber(productAddonGroup.getSequenceNumber());
        
        return productAddOnGroupRepository.save(data);                                
    }

    public Optional<ProductAddOnGroup> getById(String id){

        Optional<ProductAddOnGroup> optTemplateItem = productAddOnGroupRepository.findById(id);

        return optTemplateItem;
    }

    public Boolean deleteProductAddOnGroup(String id){
        
        Optional<ProductAddOnGroup> optTemplateItem = productAddOnGroupRepository.findById(id);

        if (optTemplateItem.isPresent()){
            productAddOnGroupRepository.deleteById(id);
            return true;

        } else{
            return false;
        }
    }
}
