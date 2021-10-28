package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.store.StoreAsset;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.StoreAssetRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.ProductRepository;
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
    
    @Value("${store.assets.url:https://symplified.ai/store-assets}")
    private String storeAssetsBaseUrl;
    
    @Value("${store.logo.default.url:https://symplified.ai/store-assets/logo_symplified_bg.png}")
    private String storeLogoDefaultUrl;
    
    @Value("${store.banner.ecommerce.default.url:https://symplified.ai/store-assets/banner-ecomm.jpeg}")
    private String storeBannerEcommerceDefaultUrl;
    
    @Value("${store.banner.fnb.default.url:https://symplified.ai/store-assets/banner-fnb.png}")
    private String storeBannerFnbDefaultUrl;

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
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        response.setStatus(HttpStatus.OK);
        response.setData(storeAssetRepository.findById(storeId));
        return ResponseEntity.status(response.getStatus()).body(response);
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
//            response.setStatus(HttpStatus.NOT_FOUND);
//    response.setError (
//            
//
//    "store not found");
//            return ResponseEntity.status(response.getStatus()).body(response);
//        }
//        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
//
//        Optional<StoreAsset> optStoreAsset = storeAssetRepository.findById(id);
//
//        if (!optStoreAsset.isPresent()) {
//            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store asset NOT_FOUND store assetId: " + id);
//            response.setStatus(HttpStatus.NOT_FOUND);
//    response.setError (
//            
//
//    "store asset not found");
//            return ResponseEntity.status(response.getStatus()).body(response);
//        }
//
//        response.setStatus(HttpStatus.OK);
//        response.setData(optStoreAsset.get());
//        return ResponseEntity.status(response.getStatus()).body(response);
//    }
    @DeleteMapping(path = {"/banner"}, name = "store-assets-banner-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-assets-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreBannerById(HttpServletRequest request,
            @PathVariable String storeId) {
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

        Optional<StoreAsset> optStoreAsset = storeAssetRepository.findById(storeId);

        if (!optStoreAsset.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store asset NOT_FOUND store assetId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store asset not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        StoreAsset storeAsset = optStoreAsset.get();
        //set default value
        Store storeInfo = optStore.get();
        if (storeInfo.getVerticalCode()!=null) {                
            if (storeInfo.getVerticalCode().toUpperCase().contains("FNB")) {
                storeAsset.setBannerUrl(storeBannerFnbDefaultUrl);
                storeAsset.setBannerMobileUrl(storeBannerFnbDefaultUrl);
            }
        } else {
            storeAsset.setBannerMobileUrl(storeBannerEcommerceDefaultUrl);
        }
        storeAssetRepository.save(storeAsset);
        
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    
    
    @DeleteMapping(path = {"/logo"}, name = "store-assets-logo-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-assets-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreLogoById(HttpServletRequest request,
            @PathVariable String storeId) {
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

        Optional<StoreAsset> optStoreAsset = storeAssetRepository.findById(storeId);

        if (!optStoreAsset.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store asset NOT_FOUND store assetId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store asset not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        StoreAsset storeAsset = optStoreAsset.get();
        storeAsset.setLogoUrl(storeLogoDefaultUrl);
        storeAssetRepository.save(storeAsset);
        
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
   
    @PostMapping(path = {""}, name = "store-assets-post")
    @PreAuthorize("hasAnyAuthority('store-assets-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreAssets(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(name = "logo", required = false) MultipartFile logo,
            @RequestParam(name = "banner", required = false) MultipartFile banner,
            @RequestParam(name = "bannerMobile", required = false) MultipartFile bannerMobile
            ) {
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
        
        Optional<StoreAsset> storeAssetOpt = storeAssetRepository.findById(storeId);
        StoreAsset storeAsset = null;
        if (storeAssetOpt.isPresent()) {
            storeAsset = storeAssetOpt.get();
        } else {
            storeAsset = new StoreAsset();
        }
        if (null != banner) {
            //user upload new banner
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "banner Filename: " + banner.getOriginalFilename());
            String bannerStoragePath = fileStorageService.saveStoreAsset(banner, storeId + "-banner");
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "banner storagePath: " + bannerStoragePath);
            storeAsset.setBannerUrl(storeAssetsBaseUrl + storeId + "-banner");
        } else if (storeAsset.getBannerUrl()==null) {
            //set default value
            Store storeInfo = optStore.get();
            if (storeInfo.getVerticalCode()!=null) {                
                if (storeInfo.getVerticalCode().toUpperCase().contains("FNB")) {
                    storeAsset.setBannerUrl(storeBannerFnbDefaultUrl);
                }
            } else {
                storeAsset.setBannerUrl(storeBannerEcommerceDefaultUrl);
            }
        }
        
        if (null != bannerMobile) {
            //user upload new banner mobile
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "banner Filename: " + banner.getOriginalFilename());
            String bannerStoragePath = fileStorageService.saveStoreAsset(banner, storeId + "-banner-mobile");
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "banner storagePath: " + bannerStoragePath);
            storeAsset.setBannerMobileUrl(storeAssetsBaseUrl + storeId + "-banner-mobile");
        } else if (storeAsset.getBannerMobileUrl()==null) {
            //set default value
            Store storeInfo = optStore.get();
            if (storeInfo.getVerticalCode()!=null) {                
                if (storeInfo.getVerticalCode().toUpperCase().contains("FNB")) {
                    storeAsset.setBannerMobileUrl(storeBannerFnbDefaultUrl);
                }
            } else {
                storeAsset.setBannerMobileUrl(storeBannerEcommerceDefaultUrl);
            }
        }

        if (null != logo) {
            //user upload new logo
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "logo Filename: " + logo.getOriginalFilename());
            String logoStoragePath = fileStorageService.saveStoreAsset(logo, storeId + "-logo");
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "logo storagePath: " + logoStoragePath);
            storeAsset.setLogoUrl(storeAssetsBaseUrl + storeId + "-logo");
        } else if (storeAsset.getLogoUrl()==null) {
            //set default logo
            storeAsset.setLogoUrl(storeLogoDefaultUrl);
        }

        storeAsset.setStoreId(storeId);
        response.setStatus(HttpStatus.OK);
        response.setData(storeAssetRepository.save(storeAsset));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
