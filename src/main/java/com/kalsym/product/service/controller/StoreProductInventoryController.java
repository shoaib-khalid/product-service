package com.kalsym.product.service.controller;

import com.kalsym.product.service.HashmapLoader;
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.DiscountCalculationType;
import com.kalsym.product.service.model.ItemDiscount;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductInventoryWithDetails;
import com.kalsym.product.service.model.product.ProductInventoryWithProductDetails;
import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.ProductInventoryRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.repository.ProductInventoryWithDetailsRepository;
import com.kalsym.product.service.repository.ProductInventoryWithProductDetailsRepository;
import com.kalsym.product.service.repository.RegionCountriesRepository;
import com.kalsym.product.service.repository.StoreDiscountRepository;
import com.kalsym.product.service.utility.ProductDiscount;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/products/{productId}/inventory")
public class StoreProductInventoryController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductInventoryWithDetailsRepository productInventoryWithDetailsRepository;
    
    @Autowired
    ProductInventoryWithProductDetailsRepository productInventoryWithProductDetailsRepository;

    @Autowired
    ProductInventoryRepository productInventoryRepository;

    @Autowired
    StoreRepository storeRepository;
    
    @Autowired
    StoreDiscountRepository storeDiscountRepository;
    
    @Autowired
    private HashmapLoader hashmapLoader;
    
    @Autowired
    RegionCountriesRepository regionCountriesRepository;
    
    @GetMapping(path = {""}, name = "store-product-inventory-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductInventorys(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestParam List<String> variantIds) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern,
                ProductServiceApplication.VERSION, logprefix, " FOUND product: " + optProdcut);

        List<ProductInventoryWithDetails> productInventorys = productInventoryWithDetailsRepository.findByProductId(productId);

        Logger.application.info(Logger.pattern,
                ProductServiceApplication.VERSION, logprefix, " FOUND Product Inventories of size: " + productInventorys.size());

        if (variantIds == null) {
            response.setData(productInventorys);
            response.setStatus(HttpStatus.OK);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        List<ProductInventoryWithDetails> returnProductInventorys = new ArrayList<>();
        for (int i = 0; i < productInventorys.size(); i++) {
            if (null != productInventorys.get(i).getProductInventoryItems()
                    && !productInventorys.get(i).getProductInventoryItems().isEmpty()) {

        Logger.application.info(Logger.pattern,
        ProductServiceApplication.VERSION, logprefix, " Inside for loop first if: " + productInventorys.get(i).getProductInventoryItems());

                if (productInventorys.get(i).getProductInventoryItems().size() == 1) {
                    String ii1Id = productInventorys.get(i).getProductInventoryItems().get(0).getProductVariantAvailableId();

                    if (ii1Id.equalsIgnoreCase(variantIds.get(0))) {
                        returnProductInventorys.add(productInventorys.get(i));
                    }
                }

                if (productInventorys.get(i).getProductInventoryItems().size() == 2) {
                    String ii1Id = productInventorys.get(i).getProductInventoryItems().get(0).getProductVariantAvailableId();
                    String ii2Id = productInventorys.get(i).getProductInventoryItems().get(1).getProductVariantAvailableId();

                    if ((ii1Id.equalsIgnoreCase(variantIds.get(0)) && ii2Id.equalsIgnoreCase(variantIds.get(1)))
                            || (ii1Id.equalsIgnoreCase(variantIds.get(1)) && ii2Id.equalsIgnoreCase(variantIds.get(0)))) {
                        returnProductInventorys.add(productInventorys.get(i));
                    }
                }
            }
        }

        response.setData(returnProductInventorys);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "store-product-inventory-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductInventorysById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND productId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<ProductInventoryWithProductDetails> optProductInventory = productInventoryWithProductDetailsRepository.findById(id);

        if (!optProductInventory.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("inventory not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }
        
        //retrieve discount info
        ProductInventoryWithProductDetails productInventory = optProductInventory.get();                
        //ItemDiscount discountDetails = discountedItemMap.get(productInventory.getItemCode());
        //ItemDiscount discountDetails = hashmapLoader.GetDiscountedItemMap(storeId, productInventory.getItemCode());
        ItemDiscount discountDetails = ProductDiscount.getItemDiscount(storeDiscountRepository, storeId, productInventory.getItemCode(), regionCountry);
        if (discountDetails!=null) {
            double discountedPrice = productInventory.getPrice();
            double dineInDiscountedPrice = productInventory.getDineInPrice();
            if (discountDetails.calculationType.equals(DiscountCalculationType.FIX)) {
                discountedPrice = productInventory.getPrice() - discountDetails.discountAmount;
            } else if (discountDetails.calculationType.equals(DiscountCalculationType.PERCENT)) {
                discountedPrice = productInventory.getPrice() - (discountDetails.discountAmount / 100 * productInventory.getPrice());
            }

            if(discountDetails.dineInCalculationType!=null && discountDetails.dineInCalculationType.equals(DiscountCalculationType.FIX)){
                dineInDiscountedPrice = productInventory.getDineInPrice() - discountDetails.dineInDiscountAmount;

            }
            else if (discountDetails.dineInCalculationType!=null && discountDetails.dineInCalculationType.equals(DiscountCalculationType.PERCENT)) {
                dineInDiscountedPrice = productInventory.getDineInPrice() - (discountDetails.dineInDiscountAmount / 100 * productInventory.getDineInPrice());
            }

            discountDetails.discountedPrice = discountedPrice;
            discountDetails.normalPrice = productInventory.getPrice();  
            discountDetails.dineInDiscountedPrice= dineInDiscountedPrice;
            discountDetails.dineInNormalPrice = productInventory.getDineInPrice();

            productInventory.setItemDiscount(discountDetails);             
        }        
        
        response.setStatus(HttpStatus.OK);
        response.setData(productInventory);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-product-inventory-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreProductInventorysById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND productId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND productId: " + productId);

        Optional<ProductInventory> optProductInventory = productInventoryRepository.findById(id);

        if (!optProductInventory.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("inventory not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND inventoryId: " + id);

        ProductInventory pi = optProductInventory.get();
        productInventoryRepository.delete(pi);

        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "store-product-inventory-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreProductInventorys(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestBody ProductInventory productInventory) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        productInventory.setProductId(productId);
        // if new client for delivery, we auto set the dine in price reduce 15%
        if (productInventory.getDineInPrice()==null) {
            productInventory.setDineInPrice(productInventory.getPrice()*0.85);
        }

        // if new client for dinein we auto set for delivery price  Increase 17.5%
        if (productInventory.getPrice()==null) {
            productInventory.setPrice(productInventory.getDineInPrice()*1.175);
        }

        if (productInventory.getCostPrice()==null) {
            productInventory.setCostPrice(0.00);
        }

        //productInventory.setProduct(optProdcut.get());
        response.setStatus(HttpStatus.OK);
        response.setData(productInventoryRepository.save(productInventory));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {"/bulk"}, name = "store-product-inventory-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postBulkStoreProductInventorys(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestBody List<ProductInventory> productInventoryList) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        


        List<ProductInventory> existingProductInventory = productInventoryRepository.findByProductId(productId);

        //if the existing size same then we just updte data
        if(existingProductInventory.size() == productInventoryList.size()){
            
            //to handle case fior tem code aa if it is variant type
            if(existingProductInventory.size() == 1){

                //delete it first
                for (ProductInventory pi : existingProductInventory) {
                    productInventoryRepository.delete(pi);
                }
                
                //then create new one
                for (int i=0; i<productInventoryList.size(); i++) {
                    
                    ProductInventory pi = productInventoryList.get(i);
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "ProductInventory["+i+"]:"+pi.toString());
                    
                    // if delivery, we auto set the dine in price 15%
                    if (pi.getDineInPrice()==null) {
                        pi.setDineInPrice(pi.getPrice()*0.85);
                    }

                    // if dinein we auto set for delivery price  Increase 17.5%
                    if (pi.getPrice()==null) {
                        pi.setPrice(pi.getDineInPrice()*1.175);
                    }

                    if (pi.getCostPrice()==null) {
                        pi.setCostPrice(0.00);
                    }

                    productInventoryRepository.save(pi);
              
                }

            } else{

                for (int i=0; i<productInventoryList.size(); i++) {

                    ProductInventory previousData = productInventoryRepository.findById(productInventoryList.get(i).getItemCode()).get(); 
                    previousData.setCompareAtprice(productInventoryList.get(i).getCompareAtprice());
                    previousData.setItemCode(productInventoryList.get(i).getItemCode());
                    previousData.setPrice(productInventoryList.get(i).getPrice());
                    previousData.setProductId(productInventoryList.get(i).getProductId());
                    previousData.setQuantity(productInventoryList.get(i).getQuantity());
                    previousData.setSKU(productInventoryList.get(i).getSKU());
                    previousData.setStatus(productInventoryList.get(i).getStatus());
                    previousData.setBarcode(productInventoryList.get(i).getBarcode());
                    previousData.setCostPrice((productInventoryList.get(i).getCostPrice()));
                    
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "ProductInventory["+i+"] price:"+productInventoryList.get(i).getPrice()+" dineInPrice:"+productInventoryList.get(i).getDineInPrice());
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "ProductInventory["+i+"] PrevPrice:"+previousData.getPrice()+" PrevDineInPrice:"+previousData.getDineInPrice());
                    
                         // if delivery, we auto set the dine in price 15%
                        if (previousData.getDineInPrice()==null) {
                            previousData.setDineInPrice(productInventoryList.get(i).getPrice()*0.85);
                        } else if (productInventoryList.get(i).getDineInPrice() == null) {
                            previousData.setDineInPrice(productInventoryList.get(i).getPrice()*0.85);
                        } else{
                            previousData.setDineInPrice(productInventoryList.get(i).getDineInPrice());
                        }
    
                        // if dinein we auto set for delivery price  Increase 17.5%
                        if (previousData.getPrice()==null) {
                            previousData.setPrice(productInventoryList.get(i).getDineInPrice()*1.175);
                        } else if (productInventoryList.get(i).getPrice()==null) {
                            previousData.setPrice(productInventoryList.get(i).getDineInPrice()*1.175);
                        } else{
                            previousData.setPrice(productInventoryList.get(i).getPrice());

                        }

                        if (previousData.getCostPrice()==null) {
                            previousData.setCostPrice(0.00);
                        } else if (productInventoryList.get(i).getCostPrice()==null) {
                            previousData.setCostPrice(0.00);
                        } else {
                            previousData.setCostPrice(productInventoryList.get(i).getCostPrice());
                        }
                    productInventoryRepository.save(previousData);
                }

            }

        } else{

            //delete first the existing data if size not same
            if(existingProductInventory.size() != 0){
                    
                for (int i=0; i<existingProductInventory.size(); i++) {
                
                    ProductInventory pi = existingProductInventory.get(i);
                    productInventoryRepository.delete(pi);
        
                }
            }

            for (int i=0; i<productInventoryList.size(); i++) {
        
                ProductInventory pi = productInventoryList.get(i);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "ProductInventory["+i+"]:"+pi.toString());
                
                // if delivery, we auto set the dine in price 15%
                if (pi.getDineInPrice()==null) {
                    pi.setDineInPrice(pi.getPrice()*0.85);
                }

                // if dinein we auto set for delivery price  Increase 17.5%
                if (pi.getPrice()==null) {
                    pi.setPrice(pi.getDineInPrice()*1.175);
                }

                if (pi.getCostPrice()==null) {
                    pi.setCostPrice(0.00);
                }

                productInventoryRepository.save(pi);
          
            }

        }

        List<ProductInventory> data = productInventoryRepository.findByProductId(productId);

        response.setStatus(HttpStatus.OK);
        response.setData(data);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
    @PutMapping(path = {"/{id}"}, name = "store-product-inventory-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-put-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> putStoreProductInventorysById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @PathVariable String id,
            @RequestBody ProductInventory bodyProductInventory) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

         Optional<ProductInventory> optProductInventory = productInventoryRepository.findById(id);

        if (!optProductInventory.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("inventory not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND inventoryId: " + id);
 
        // if delivery, we auto set the dine in price reduce 15%
        if (bodyProductInventory.getDineInPrice()==null) {
            bodyProductInventory.setDineInPrice(bodyProductInventory.getPrice()*0.85);
        }

        // if dinein we auto set for delivery price  Increase 17.5%
        if (bodyProductInventory.getPrice()==null) {
            bodyProductInventory.setPrice(bodyProductInventory.getDineInPrice()*1.175);
        }

        if (bodyProductInventory.getCostPrice()==null) {
            bodyProductInventory.setCostPrice(0.00);
        }

        ProductInventory pi = optProductInventory.get();
        pi.update(bodyProductInventory);

        response.setStatus(HttpStatus.OK);
        response.setData(productInventoryRepository.save(pi));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
