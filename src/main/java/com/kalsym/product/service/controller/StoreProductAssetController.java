package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductAsset;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.ProductAssetRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.service.FileStorageService;
import com.kalsym.product.service.utility.Logger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PutMapping;
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

    @Value("${asset.service.url}")
    String assetServiceUrl;

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

        //to append the asset url
        List<ProductAsset> productAssetList = productAssetRepository.findByProductId(productId);
        for (ProductAsset p : productAssetList ){

            //handle null
            if(p.getUrl() != null){
                p.setUrl(assetServiceUrl+p.getUrl());

            } else {
                p.setUrl(null);

            }

        }

        response.setStatus(HttpStatus.OK);
        response.setData(productAssetList);
        return ResponseEntity.status(response.getStatus()).body(response);
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

        Optional<ProductAsset> optProductAsset = productAssetRepository.findById(id);

        if (!optProductAsset.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("inventory not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        //to append the asset url
        //handle null
        if(optProductAsset.get().getUrl() != null){
            optProductAsset.get().setUrl(assetServiceUrl+optProductAsset.get().getUrl());

        } else{
            optProductAsset.get().setUrl(null);

        }
        response.setStatus(HttpStatus.OK);
        response.setData(optProductAsset.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-product-assets-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-product-assets-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
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

        Optional<ProductAsset> optProductAsset = productAssetRepository.findById(id);

        List<ProductAsset> listOfProductAsset = productAssetRepository.findByProductId(productId);

        if (!optProductAsset.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product asset NOT_FOUND inventoryId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product asset not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        List<ProductAsset> filtered;
        filtered = listOfProductAsset.stream()
        .filter(productasset -> !productasset.getId().equals(id))
        .collect(Collectors.toList());
        
        Product product = optProdcut.get();
        if (optProductAsset.get().getUrl().equals(product.getThumbnailUrl())) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleting thumbail: " + id);
            // product.setThumbnailUrl(null);

            // productRepository.save(product);

            //to set default image if it not set after delete image default
            if(filtered.size()>0){
                this.setDefaultThumbnail(filtered, product);

            }else{

                product.setThumbnailUrl(null);
                productRepository.save(product);
 
            }
        }

        productAssetRepository.delete(optProductAsset.get());

        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "store-product-assets-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-product-assets-put-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> putStoreProductAssetsById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @PathVariable String id,
            @RequestBody ProductAsset productAssetBody) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND productId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND productId: " + productId);

        Optional<ProductAsset> optProductAssset = productAssetRepository.findById(id);

        if (!optProductAssset.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product asset NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product asset not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND product asset Id: " + id);

        ProductAsset productAsset = optProductAssset.get();
        //HANDLIN PRODUCT ASSET FOR PAYLOAD
        // String split[] = productAssetBody.getUrl().split("/product-assets", 0);
        // String pathUrl = "/product-assets"+split[1];

        // productAssetBody.setUrl(pathUrl);
        productAsset.update(productAssetBody);

        productAsset = productAssetRepository.save(productAsset);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "saved image: " + productAsset.getId());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "isThumbnail: " + productAsset.getIsThumbnail());

        Product product = optProdcut.get();

        //if is thumbnail true then remove thumbnail true from all other assets
        if (productAsset.getIsThumbnail() == true) {

            product.setThumbnailUrl(productAsset.getUrl());
            productRepository.save(product);
            List<ProductAsset> productAssets = productAssetRepository.findByProductId(productId);

            for (ProductAsset productA : productAssets) {
                if (!productA.getId().equals(productAsset.getId())) {
                    productA.setIsThumbnail(false);
                    productAssetRepository.save(productA);
                }
            }
        }

        //to display data full url after save
        productAsset.setUrl(assetServiceUrl+productAsset.getUrl());
        
        response.setStatus(HttpStatus.OK);
        response.setData(productAsset);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // @Value("${product.assets.url:https://symplified.ai/product-assets/}")
    // private String productAssetsBaseUrl;

    @PostMapping(path = {""}, name = "store-product-assets-post")
    @PreAuthorize("hasAnyAuthority('store-product-assets-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreProductAssets(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestParam(value="file",required = false) MultipartFile file,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) Boolean isThumbnail) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND productId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        String storagePath;
        String generatedUrl;
        ProductAsset productAsset = new ProductAsset();

        //if file exist then we keep in server storage or we use url only
        if(file != null){
           
            //if item code exist then delete the existing
            if (itemCode != null) {
                Optional<ProductAsset> optProdAsset = productAssetRepository.findByItemCode(itemCode);
                if (optProdAsset.isPresent()) {
                    productAssetRepository.deleteById(optProdAsset.get().getId());
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Existing asset deleted successfully");
                }
                generatedUrl = itemCode + fileStorageService.generateRandomName();
                storagePath = fileStorageService.saveProductAsset(file, generatedUrl);            
            } 
            //create a new item code of image
            else {
                //
                generatedUrl = productId + fileStorageService.generateRandomName();
                storagePath = fileStorageService.saveProductAsset(file, generatedUrl);            
            }

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "storagePath: " + storagePath);

            productAsset.setProductId(productId);
            productAsset.setName(file.getOriginalFilename());
            productAsset.setItemCode(itemCode);
            //productAsset.setIsThumbnail(isThumbnail);
            productAsset.setUrl("/product-assets/"+generatedUrl);

            productAsset = productAssetRepository.save(productAsset);

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "saved image: " + productAsset.getId());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "isThumbnail: " + isThumbnail);
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND productId: " + storeId);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "OriginalFilename: " + file.getOriginalFilename());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Checking if this asset already exists");

        }
        //insert the url of image
        else{

            //with item code
            if (itemCode != null) {

                Optional<ProductAsset> optProdAsset = productAssetRepository.findByItemCode(itemCode);
                if (optProdAsset.isPresent()) {
                    productAssetRepository.deleteById(optProdAsset.get().getId());
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Existing asset deleted successfully");
                }
   
                productAsset.setItemCode(itemCode);

            }

            String split[] = url.split("/product-assets", 0);
            String pathUrl = "/product-assets"+split[1];

            productAsset.setProductId(productId);
            productAsset.setName("Clone");

            productAsset.setUrl(pathUrl);
            productAsset = productAssetRepository.save(productAsset);


        }
        
        Product product = optProdcut.get();
        List<ProductAsset> productAssets = productAssetRepository.findByProductId(productId);
        //if is thumbnail true then remove thumbnail true from all other assets
        if (isThumbnail) {

            product.setThumbnailUrl(productAsset.getUrl());
            productRepository.save(product);

            for (ProductAsset productA : productAssets) {
                if (!productA.getId().equals(productAsset.getId())) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "isThumbnail: " + isThumbnail);

                    productA.setIsThumbnail(false);
                    productAssetRepository.save(productA);
                } else {
                    productA.setIsThumbnail(true);
                    productAssetRepository.save(productA);
                }
            }
        } else {
            this.setDefaultThumbnail(productAssets, product);
        }

        //to display data full url after save
        productAsset.setUrl(assetServiceUrl+productAsset.getUrl());
    

        response.setStatus(HttpStatus.OK);
        response.setData(productAsset);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @PutMapping(path = {"/update"}, name = "store-product-assets-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-product-assets-put-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> updateStoreProductAssetsById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = true) String id,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) Boolean isThumbnail) {
        
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(productId);

        if (!optProdcut.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND productId: " + productId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND productId: " + storeId);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "OriginalFilename: " + file.getOriginalFilename());
        
         Optional<ProductAsset> optProductAssset = productAssetRepository.findById(id);

        if (!optProductAssset.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product asset NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product asset not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND product asset Id: " + id);
        
        String storagePath;
        String generatedUrl;       
        storagePath = fileStorageService.saveProductAsset(file, itemCode + file.getOriginalFilename().replaceAll("[^A-Za-z0-9]", ""));
        generatedUrl = itemCode + file.getOriginalFilename().replaceAll("[^A-Za-z0-9]", "");
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "storagePath: " + storagePath);

        ProductAsset productAsset = new ProductAsset();
        productAsset.setProductId(productId);
        productAsset.setName(file.getOriginalFilename());
        productAsset.setItemCode(itemCode);
        productAsset.setUrl("/product-assets/" + generatedUrl);
        productAsset.setId(optProductAssset.get().getId());
        productAsset = productAssetRepository.save(productAsset);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "saved image: " + productAsset.getId());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "isThumbnail: " + isThumbnail);

        Product product = optProdcut.get();
        List<ProductAsset> productAssets = productAssetRepository.findByProductId(productId);
        //if is thumbnail true then remove thumbnail true from all other assets
        if (isThumbnail) {

            product.setThumbnailUrl(productAsset.getUrl());
            productRepository.save(product);

            for (ProductAsset productA : productAssets) {
                if (!productA.getId().equals(productAsset.getId())) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "isThumbnail: " + isThumbnail);

                    productA.setIsThumbnail(false);
                    productAssetRepository.save(productA);
                } else {
                    productA.setIsThumbnail(true);
                    productAssetRepository.save(productA);
                }
            }
        } else {
            this.setDefaultThumbnail(productAssets, product);
        }

        //to display data full url after save
        productAsset.setUrl(assetServiceUrl+productAsset.getUrl());

        response.setStatus(HttpStatus.OK);
        response.setData(productAsset);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private void setDefaultThumbnail(List<ProductAsset> productAssets, Product product) {
        for (ProductAsset pA : productAssets) {
            if (pA.getIsThumbnail() != null && pA.getIsThumbnail()) {
                return;
            }
        }
        if (productAssets.get(0) != null) {
            ProductAsset pA = productAssets.get(0);
            pA.setIsThumbnail(true);
            product.setThumbnailUrl(pA.getUrl());
            productRepository.save(product);
            productAssetRepository.save(pA);
        }
    }
}
