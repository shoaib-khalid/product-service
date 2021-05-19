package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductInventoryWithDetails;
import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.repository.ProductInventoryRepository;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.model.repository.ProductRepository;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.model.repository.ProductInventoryWithDetailsRepository;
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
    ProductInventoryRepository productInventoryRepository;

    @Autowired
    StoreRepository storeRepository;

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
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "store not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + productId);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<ProductInventoryWithDetails> productInventorys = productInventoryWithDetailsRepository.findByProductId(productId);

        if (variantIds == null) {
            response.setData(productInventorys);
            response.setSuccessStatus(HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        List<ProductInventoryWithDetails> returnProductInventorys = new ArrayList<>();
        for (int i = 0; i < productInventorys.size(); i++) {
            if (null != productInventorys.get(i).getProductInventoryItems()
                    && !productInventorys.get(i).getProductInventoryItems().isEmpty()) {

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
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "store not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND productId: " + productId);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<ProductInventoryWithDetails> optProductInventory = productInventoryWithDetailsRepository.findById(id);

        if (!optProductInventory.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "inventory not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optProductInventory.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-product-inventory-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-delete-by-id', 'all')")
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
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "store not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND productId: " + productId);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND productId: " + productId);

        Optional<ProductInventory> optProductInventory = productInventoryRepository.findById(id);

        if (!optProductInventory.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "inventory not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND inventoryId: " + id);

        ProductInventory pi = optProductInventory.get();
        productInventoryRepository.delete(pi);

        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {""}, name = "store-product-inventory-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-inventory-post', 'all')")
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
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "store not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + productId);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        productInventory.setProductId(productId);
        //productInventory.setProduct(optProdcut.get());
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productInventoryRepository.save(productInventory));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}