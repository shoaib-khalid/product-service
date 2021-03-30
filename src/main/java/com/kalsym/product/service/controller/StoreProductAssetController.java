package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductAsset;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.repository.ProductAssetRepository;
import com.kalsym.product.service.model.repository.ProductInventoryRepository;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.model.repository.ProductRepository;
import com.kalsym.product.service.model.repository.ProductInventoryRepository;
import com.kalsym.product.service.model.repository.ProductReviewRepository;
import com.kalsym.product.service.service.FileStorageService;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/products/{productId}/assets")
public class StoreProductAssetController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductAssetRepository productAssetRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""}, name = "store-product-assets-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-assets-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductAssets(HttpServletRequest request,
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
        response.setData(productAssetRepository.findByProductId(productId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "store-product-assets-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-assets-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductAssetsById(HttpServletRequest request,
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

        Optional<ProductAsset> optProductAsset = productAssetRepository.findById(id);

        if (!optProductAsset.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "inventory not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optProductAsset.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-product-assets-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-assets-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteStoreProductAssetsById(HttpServletRequest request,
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

        Optional<ProductAsset> optProductAsset = productAssetRepository.findById(id);

        if (!optProductAsset.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "inventory not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        productAssetRepository.delete(optProductAsset.get());

        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Value("${product.assets.url:https://symplified.ai/assets/product-assets/}")
    private String productAssetsBaseUrl;

    @PostMapping(path = {""}, name = "store-product-assets-post")
    @PreAuthorize("hasAnyAuthority('store-product-assets-post', 'all')")
    public ResponseEntity<HttpResponse> postStoreProductAssets(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String itemCode) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "store not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND productId: " + productId);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND productId: " + storeId);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "OriginalFilename: " + file.getOriginalFilename());

        String storagePath = fileStorageService.saveProductAsset(file, file.getOriginalFilename());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "storagePath: " + storagePath);

        ProductAsset productAsset = new ProductAsset();
        productAsset.setProductId(productId);
        productAsset.setName(file.getOriginalFilename());
        productAsset.setItemCode(itemCode);
        productAsset.setUrl(productAssetsBaseUrl + file.getOriginalFilename());
        //productAsset.setProduct(optProdcut.get());
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productAssetRepository.save(productAsset));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
