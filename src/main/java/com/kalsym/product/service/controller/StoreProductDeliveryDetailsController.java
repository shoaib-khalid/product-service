package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductDeliveryDetail;
import com.kalsym.product.service.model.Store;
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
import com.kalsym.product.service.model.repository.ProductDeliveryDetailsRepository;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/products/{productId}/deliverydetails")
public class StoreProductDeliveryDetailsController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductDeliveryDetailsRepository productDeliveryDetailsRepository;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""}, name = "store-product-delivery-details-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-delivery-details-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductDeliveryDetails(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId) {
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

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productDeliveryDetailsRepository.findById(productId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "store-product-delivery-details-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-delivery-details-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductDeliveryDetailsById(HttpServletRequest request,
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

        Optional<ProductDeliveryDetail> optProductDeliveryDetails = productDeliveryDetailsRepository.findById(id);

        if (!optProductDeliveryDetails.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deliverydetails NOT_FOUND deliverydetailsId: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "deliverydetails not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optProductDeliveryDetails.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-product-delivery-details-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-delivery-details-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteStoreProductDeliveryDetailsById(HttpServletRequest request,
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

        Optional<ProductDeliveryDetail> optProductDeliveryDetails = productDeliveryDetailsRepository.findById(id);

        if (!optProductDeliveryDetails.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deliverydetails NOT_FOUND deliverydetailsId: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "deliverydetails not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND deliverydetailsId: " + id);

        ProductDeliveryDetail pi = optProductDeliveryDetails.get();
        productDeliveryDetailsRepository.delete(pi);

        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {""}, name = "store-product-delivery-details-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-delivery-details-post', 'all')")
    public ResponseEntity<HttpResponse> postStoreProductDeliveryDetails(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestBody ProductDeliveryDetail productInventory) {
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
        response.setData(productDeliveryDetailsRepository.save(productInventory));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
