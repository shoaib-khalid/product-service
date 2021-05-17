package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.StoreAsset;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.repository.StoreAssetRepository;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.model.repository.ProductRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/assets")
public class StoreAssetController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreAssetRepository storeAssetRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""}, name = "store-assets-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-assets-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreAssets(HttpServletRequest request,
            @PathVariable String storeId) {
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

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(storeAssetRepository.findById(storeId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @GetMapping(path = {"/{id}"}, name = "store-assets-get-by-id", produces = "application/json")
//    @PreAuthorize("hasAnyAuthority('store-assets-get-by-id', 'all')")
//    public ResponseEntity<HttpResponse> getStoreAssetsById(HttpServletRequest request,
//            @PathVariable String storeId,
//            @PathVariable String id) {
//        String logprefix = request.getRequestURI();
//        HttpResponse response = new HttpResponse(request.getRequestURI());
//
//        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);
//
//        Optional<Store> optStore = storeRepository.findById(storeId);
//
//        if (!optStore.isPresent()) {
//            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
//            response.setSuccessStatus(HttpStatus.NOT_FOUND, "store not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
//
//        Optional<StoreAsset> optStoreAsset = storeAssetRepository.findById(id);
//
//        if (!optStoreAsset.isPresent()) {
//            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
//            response.setSuccessStatus(HttpStatus.NOT_FOUND, "inventory not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//
//        response.setSuccessStatus(HttpStatus.OK);
//        response.setData(optStoreAsset.get());
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
    @DeleteMapping(path = {"/{id}"}, name = "store-assets-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-assets-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteStoreAssetsById(HttpServletRequest request,
            @PathVariable String storeId,
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

        Optional<StoreAsset> optStoreAsset = storeAssetRepository.findById(id);

        if (!optStoreAsset.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "inventory NOT_FOUND inventoryId: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "inventory not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        storeAssetRepository.delete(optStoreAsset.get());

        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Value("${store.assets.url:https://symplified.ai/store-assets}")
    private String storeAssetsBaseUrl;

    @PostMapping(path = {""}, name = "store-assets-post")
    @PreAuthorize("hasAnyAuthority('store-assets-post', 'all')")
    public ResponseEntity<HttpResponse> postStoreAssets(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(name = "logo", required = false) MultipartFile logo,
            @RequestParam(name = "banner", required = false) MultipartFile banner) {
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

        StoreAsset storeAsset = new StoreAsset();
        if (null != banner) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "banner Filename: " + banner.getOriginalFilename());
            String bannerStoragePath = fileStorageService.saveStoreAsset(banner, banner.getOriginalFilename());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "banner storagePath: " + bannerStoragePath);
            storeAsset.setBannerUrl(storeAssetsBaseUrl + banner.getOriginalFilename());
        }

        if (null != logo) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "logo Filename: " + logo.getOriginalFilename());
            String logoStoragePath = fileStorageService.saveStoreAsset(logo, logo.getOriginalFilename());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "logo storagePath: " + logoStoragePath);
            storeAsset.setLogoUrl(storeAssetsBaseUrl + logo.getOriginalFilename());
        }

        storeAsset.setStoreId(storeId);
        //storeAsset.setProduct(optProdcut.get());
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(storeAssetRepository.save(storeAsset));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
