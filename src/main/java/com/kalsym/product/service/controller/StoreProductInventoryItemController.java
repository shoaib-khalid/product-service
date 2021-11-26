package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.product.ProductInventoryItem;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.ProductAssetRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.ProductInventoryItemRepository;
import com.kalsym.product.service.repository.ProductReviewRepository;
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

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/products/{productId}/inventory-item")
public class StoreProductInventoryItemController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductInventoryWithDetailsRepository productInventoryRepository;

    @Autowired
    ProductInventoryItemRepository productInventoryItemRepository;


    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""}, name = "store-product-inventory-item-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-item-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductInventoryItems(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId) {
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
            response.setError( "product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(productInventoryItemRepository.findByProductId(productId));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "store-product-inventory-item-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-item-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductInventoryItemsById(HttpServletRequest request,
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
            response.setError( "product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<ProductInventoryItem> optProductInventoryItem = productInventoryItemRepository.findById(id);

        if (!optProductInventoryItem.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "varaint NOT_FOUND varaintId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("variant not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(optProductInventoryItem.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-product-inventory-item-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-item-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreProductInventoryItemsById(HttpServletRequest request,
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
            response.setError( "product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<ProductInventoryItem> optProductInventoryItem = productInventoryItemRepository.findById(id);

        if (!optProductInventoryItem.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "varaint NOT_FOUND varaintId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError( "varaint not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        productInventoryItemRepository.delete(optProductInventoryItem.get());

        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "store-product-inventory-item-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-item-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreProductInventoryItems(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestBody ProductInventoryItem productVariant) {
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
            response.setError( "product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(productInventoryItemRepository.save(productVariant));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
    @PostMapping(path = {"/bulk"}, name = "store-product-inventory-item-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-item-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreProductInventoryItemsBulk(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestBody ProductInventoryItem[] productVariantList) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "postStoreProductInventoryItemsBulk storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " postStoreProductInventoryItemsBulk NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " postStoreProductInventoryItemsBulk FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "postStoreProductInventoryItemsBulk product NOT_FOUND storeId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError( "product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        ProductInventoryItem[] productVariantStoredList = new ProductInventoryItem[productVariantList.length];
        for (int i=0;i<productVariantList.length;i++) {
            ProductInventoryItem  productVariant = productVariantList[i];
            productInventoryItemRepository.save(productVariant);
            productVariantStoredList[i] = productVariant;
        }
        
        response.setStatus(HttpStatus.OK);
        response.setData(productVariantStoredList);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
    @DeleteMapping(path = {"/bulk"}, name = "store-product-inventory-item-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-item-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreProductInventoryItemsByIdBulk(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestBody ProductInventoryItem[] productVariantList) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "deleteStoreProductInventoryItemsByIdBulk storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleteStoreProductInventoryItemsByIdBulk NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleteStoreProductInventoryItemsByIdBulk FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleteStoreProductInventoryItemsByIdBulk product NOT_FOUND productId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError( "product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        for (int i=0;i<productVariantList.length;i++) {
            ProductInventoryItem  productVariant = productVariantList[i];
            Optional<ProductInventoryItem> optProductInventoryItem = productInventoryItemRepository.findByItemCodeAndProductVariantAvailableId(productVariant.getItemCode(), productVariant.getProductVariantAvailableId());
            if (optProductInventoryItem.isPresent()) {
                productInventoryItemRepository.delete(optProductInventoryItem.get());
            }
        }
        
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @PutMapping(path = {"/{id}"}, name = "store-product-inventory-item-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-item-put-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> putStoreProductInventoryItemsById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @PathVariable String id,
            @RequestBody ProductInventoryItem bodyProductInventoryItem) {
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
            response.setError( "product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<ProductInventoryItem> optProductInventoryItem = productInventoryItemRepository.findById(id);

        if (!optProductInventoryItem.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "varaint NOT_FOUND varaintId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError( "variant not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND inventoryId: " + id);
 
        ProductInventoryItem pit = optProductInventoryItem.get();
        pit.update(bodyProductInventoryItem);

        response.setStatus(HttpStatus.OK);
        response.setData(productInventoryItemRepository.save(pit));
        return ResponseEntity.status(response.getStatus()).body(response);
                
    }

}
