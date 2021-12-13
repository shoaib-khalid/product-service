/*
 * Copyright (C) 2021 taufik
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kalsym.product.service;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.StoreDiscountType;
import com.kalsym.product.service.model.ItemDiscount;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.StoreDiscount;
import com.kalsym.product.service.model.store.StoreDiscountProduct;
import com.kalsym.product.service.model.store.StoreDiscountTier;
import com.kalsym.product.service.repository.ProductInventoryRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.RegionCountriesRepository;
import com.kalsym.product.service.repository.StoreDiscountProductRepository;
import com.kalsym.product.service.repository.StoreDiscountRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.utility.DateTimeUtil;
import com.kalsym.product.service.utility.Logger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author taufik
 */
@Component
public class HashmapLoader {
   
    @Autowired
    StoreDiscountRepository storeDiscountRepository;

    @Autowired
    StoreRepository storeRepository;
    
    @Autowired
    StoreDiscountProductRepository storeDiscountProductRepository;
    
    @Autowired
    RegionCountriesRepository regionCountriesRepository;
    
    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    ProductInventoryRepository productInventoryRepository;
    
    HashMap<String, ItemDiscount> discountedItemMap = new HashMap<String, ItemDiscount>();
    
    @Scheduled(fixedRate = 120000)
    public void LoadDiscountedItemMap() {
        String logprefix = "LoadDiscountedItemMap";        
        List<StoreDiscount> storeDiscountList = storeDiscountRepository.findAllAvailableDiscount(new Date());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Start process. Available discount found:" + storeDiscountList.size());
        for (int x=0;x<storeDiscountList.size();x++) {
            StoreDiscount discountAvailable = storeDiscountList.get(x);
            String storeId = discountAvailable.getStoreId();
            Optional<Store> optStore = storeRepository.findById(storeId);
            //get reqion country for store
            RegionCountry regionCountry = null;
            Optional<RegionCountry> t = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
            if (t.isPresent()) {
                regionCountry = t.get();
            }
        
            //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Discount id:" + discountAvailable.getId()+" type:"+discountAvailable.getDiscountType()+" name:"+discountAvailable.getDiscountName());
           // System.out.println("LoadDiscountedItemMap() -> Discount id:" + discountAvailable.getId()+" type:"+discountAvailable.getDiscountType()+" name:"+discountAvailable.getDiscountName());
            if (discountAvailable.getDiscountType().equals(StoreDiscountType.ITEM)) {
                
                //get discount amount
                LocalDateTime startLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(discountAvailable.getStartDate(), ZoneId.of(regionCountry.getTimezone()) );
                LocalDateTime endLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(discountAvailable.getEndDate(), ZoneId.of(regionCountry.getTimezone()) );
               
                //get all item
                List<StoreDiscountProduct> storeDiscountProductList = storeDiscountProductRepository.findByStoreDiscountId(discountAvailable.getId());
                //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Discount product found:" + storeDiscountProductList.size());
               // System.out.println("LoadDiscountedItemMap() -> Discount product found:" + storeDiscountProductList.size());
                for (int y=0;y<storeDiscountProductList.size();y++) {
                    StoreDiscountProduct discountProduct = storeDiscountProductList.get(y);
                    //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "ItemCode:" + discountProduct.getItemCode());
                    //System.out.println("LoadDiscountedItemMap() -> ItemCode:"+discountProduct.getItemCode()+" CategoryId:"+discountProduct.getCategoryId());
                    if (discountProduct.getItemCode()!=null) {
                        ItemDiscount discountDetails = new ItemDiscount();
                        discountDetails.normalItemOnly = discountAvailable.getNormalPriceItemOnly();
                        List<StoreDiscountTier> discountTier = discountAvailable.getStoreDiscountTierList();
                        discountDetails.calculationType = discountTier.get(0).getCalculationType();
                        discountDetails.discountAmount = discountTier.get(0).getDiscountAmount();
                        discountDetails.discountLabel = discountAvailable.getDiscountName();
                        discountDetails.discountStartTime = startLocalTime;
                        discountDetails.discountEndTime = endLocalTime;
                        discountDetails.lastUpdateTime = new Date();
                        discountDetails.discountId = discountAvailable.getId();
                        discountedItemMap.put(storeId+"|"+discountProduct.getItemCode(), discountDetails);
                        //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "itemCode:"+discountProduct.getItemCode()+" discountAmount:" + discountDetails.discountAmount + " calculationType:"+discountDetails.calculationType);
                       // System.out.println("itemCode:"+discountProduct.getItemCode()+" discountAmount:" + discountDetails.discountAmount + " calculationType:"+discountDetails.calculationType);
                    } else if (discountProduct.getCategoryId()!=null) {
                        //get all product under this category
                        List<Product> productList = productRepository.findByCategoryId(discountProduct.getCategoryId());
                        //System.out.println("Product found under this category:"+productList.size());
                        for (int z=0;z<productList.size();z++) {
                            List<ProductInventory> productInventoryList = productInventoryRepository.findByProductId(productList.get(z).getId());
                            //System.out.println("Product Inventory found under this productId:"+productList.get(z).getId()+" -> "+productInventoryList.size());
                            for (int c=0;c<productInventoryList.size();c++) {
                                ProductInventory inventory = productInventoryList.get(c);                                
                                ItemDiscount discountDetails = new ItemDiscount();
                                discountDetails.normalItemOnly = discountAvailable.getNormalPriceItemOnly();
                                List<StoreDiscountTier> discountTier = discountAvailable.getStoreDiscountTierList();
                                discountDetails.calculationType = discountTier.get(0).getCalculationType();
                                discountDetails.discountAmount = discountTier.get(0).getDiscountAmount();
                                discountDetails.discountLabel = discountAvailable.getDiscountName();
                                discountDetails.discountStartTime = startLocalTime;
                                discountDetails.discountEndTime = endLocalTime;
                                discountDetails.lastUpdateTime = new Date();
                                discountDetails.discountId = discountAvailable.getId();
                                discountedItemMap.put(storeId+"|"+inventory.getItemCode(), discountDetails);
                                //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "itemCode:"+inventory.getItemCode()+" discountAmount:" + discountDetails.discountAmount + " calculationType:"+discountDetails.calculationType);
                                //System.out.println("itemCode:"+discountProduct.getItemCode()+" discountAmount:" + discountDetails.discountAmount + " calculationType:"+discountDetails.calculationType);
                            }
                        }
                    }
                }
            }
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Finish process. loaded item:"+discountedItemMap.size());        
    }
    
    
    public ItemDiscount GetDiscountedItemMap(String storeId, String itemCode) {
        ItemDiscount discountDetails = discountedItemMap.get(storeId+"|"+itemCode);
        //System.out.println("Find in map:"+storeId+"|"+itemCode+" -> "+discountDetails);
        return discountDetails;
    }
    
    @Scheduled(fixedRate = 600000)
    public void ManageDiscountedItemMap() {
        String logprefix = "ManageDiscountedItemMap";
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Start process. hashmap size:"+discountedItemMap.size());        
       
        // using iterators
        Iterator<HashMap.Entry<String, ItemDiscount>> itr = discountedItemMap.entrySet().iterator();
        
        while(itr.hasNext()) {
            HashMap.Entry<String, ItemDiscount> entry = itr.next();        
            //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Key = " + entry.getKey() +", Value = " + entry.getValue());
            ItemDiscount discountDetails = entry.getValue();
            Date lastUpdate = discountDetails.lastUpdateTime;
            // d1, d2 are dates
            long diff = new Date().getTime() - lastUpdate.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "LastUpdate:"+lastUpdate+" was "+diffMinutes+" minutes ago"); 
            if (diffMinutes>10) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Remove from hashmap : "+entry.getKey());
                itr.remove();                
            }
        }
    }
   
}
