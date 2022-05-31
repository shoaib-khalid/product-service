package com.kalsym.product.service.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.kalsym.product.service.enums.DiscountCalculationType;
import com.kalsym.product.service.model.ItemDiscount;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.model.product.ProductInventoryWithDetails;
import com.kalsym.product.service.model.product.ProductMain;
import com.kalsym.product.service.model.store.StoreDiscountProduct;
import com.kalsym.product.service.repository.ProductMainRepository;
import com.kalsym.product.service.repository.ProductWithDetailsRepository;
import com.kalsym.product.service.repository.RegionCountriesRepository;
import com.kalsym.product.service.repository.StoreDiscountProductRepository;
import com.kalsym.product.service.repository.StoreDiscountRepository;
import com.kalsym.product.service.utility.ProductDiscount;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
public class ProductCategoryService {
    
    @Autowired
    RegionCountriesRepository regionCountriesRepository;

    @Autowired
    ProductWithDetailsRepository productWithDetailsRepository;

    @Autowired
    StoreDiscountRepository storeDiscountRepository;

    @Autowired
    StoreDiscountProductRepository storeDiscountProductRepository;

    @Autowired
    ProductMainRepository productMainRepository;


    public Page<ProductMain> getRawQueryProductCountryIdAndParentCategoryId(String regionCountryId, String parentCategoryId, List<String> status,int page, int pageSize){
    
        //Handling null value in order to use query
        if (regionCountryId == null || regionCountryId.isEmpty()) {
            regionCountryId = "";
        }

        if (status == null) {

            List<String> statusList = new ArrayList<>();
            statusList.add("ACTIVE");
            statusList.add("INACTIVE");
            statusList.add("OUTOFSTOCK");

            status = statusList;
        }

        Pageable pageable = PageRequest.of(page, pageSize);

        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(regionCountryId);
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }

        //find the based on location with pageable
        Page<ProductMain> result = productMainRepository.getProductCountryIdAndParentCategoryId(status,regionCountryId,parentCategoryId,pageable);

        //extract the result of content of pageable in order to proceed with dicount of item 
        List<ProductMain> productList = result.getContent();

        ProductMain[] productWithDetailsList = new ProductMain[productList.size()];

        for (int x=0;x<productList.size();x++) {

            //check for item discount in hashmap
            ProductMain productDetails = productList.get(x);
            for (int i=0;i<productDetails.getProductInventories().size();i++) {
                
                ProductInventoryWithDetails productInventory = productDetails.getProductInventories().get(i);
                String storeId = productDetails.getStoreDetails().getId();

                //ItemDiscount discountDetails = discountedItemMap.get(productInventory.getItemCode());
                /*ItemDiscount discountDetails = hashmapLoader.GetDiscountedItemMap(storeId, productInventory.getItemCode());*/
                ItemDiscount discountDetails = ProductDiscount.getItemDiscount(storeDiscountRepository, storeId, productInventory.getItemCode(), regionCountry);
                if (discountDetails != null) {                    
                    double discountedPrice = productInventory.getPrice();
                    if (discountDetails.calculationType.equals(DiscountCalculationType.FIX)) {
                        discountedPrice = productInventory.getPrice() - discountDetails.discountAmount;
                    } else if (discountDetails.calculationType.equals(DiscountCalculationType.PERCENT)) {
                        discountedPrice = productInventory.getPrice() - (discountDetails.discountAmount / 100 * productInventory.getPrice());
                    }
                    discountDetails.discountedPrice = discountedPrice;
                    discountDetails.normalPrice = productInventory.getPrice();                    
                    productInventory.setItemDiscount(discountDetails); 
                } else {
                    //get inactive discount if any
                    List<StoreDiscountProduct> discountList = storeDiscountProductRepository.findByItemCode(productInventory.getItemCode());
                    if (!discountList.isEmpty()) {
                        StoreDiscountProduct storeDiscountProduct = discountList.get(0);
                        ItemDiscount inactiveDiscount = new ItemDiscount();
                        inactiveDiscount.discountId = storeDiscountProduct.getStoreDiscountId();
                        productInventory.setItemDiscountInactive(inactiveDiscount);
                    }
                
                }
            }

            productWithDetailsList[x]=productDetails;

        }

        // convert array to array list
        List<ProductMain> newArrayList = new ArrayList<>(Arrays.asList(productWithDetailsList));

        //Page mapper
        Page<ProductMain> output = new PageImpl<ProductMain>(newArrayList,pageable,result.getTotalElements());
        
        return output;
    }

}