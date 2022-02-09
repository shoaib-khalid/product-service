package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.store.StoreAssets;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.enums.StoreAssetType;
import com.kalsym.product.service.repository.StoreAssetsRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.service.FileStorageService;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/storeassets")
public class StoreAssetsController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreAssetsRepository storeAssetsRepository;

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
            
    @PostMapping(path = {""}, name = "store-assets-post")
    @PreAuthorize("hasAnyAuthority('store-assets-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreAssets(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(name = "assetFile", required = true) MultipartFile assetFile,
            @RequestParam(name = "assetType", required = true) StoreAssetType assetType,
            @RequestParam(name = "assetDescription", required = true) String assetDescription
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
        
        StoreAssets storeAsset = new StoreAssets();
        storeAsset.setAssetFile(assetFile);
        storeAsset.setAssetType(assetType);
        storeAsset.setAssetDescription(assetDescription);
        storeAsset.setStoreId(storeId);
                
        String generatedUrl;       
        generatedUrl = storeId + fileStorageService.generateRandomName();
        String logoStoragePath = fileStorageService.saveMultipleStoreAssets(storeAsset.getAssetFile(), generatedUrl);
                
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Asset Filename: " + storeAsset.getAssetFile().getOriginalFilename());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Asset storagePath: " + logoStoragePath);
        storeAsset.setAssetUrl(storeAssetsBaseUrl + generatedUrl);                
        
        storeAssetsRepository.save(storeAsset);
        
        response.setStatus(HttpStatus.OK);
        storeAsset.setAssetFile(null);
        response.setData(storeAsset);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
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
        
        List<StoreAssets> storeAssetsList = storeAssetsRepository.findByStoreId(storeId);
        
        storeAssetsList = SetDefaultAsset(optStore.get(), storeId, storeAssetsList);
        
        response.setStatus(HttpStatus.OK);
        response.setData(storeAssetsList);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-assets-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-assets-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreAssetsById(HttpServletRequest request,
            @PathVariable String storeId,
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

        Optional<StoreAssets> optStoreAsset = storeAssetsRepository.findById(id);

        if (!optStoreAsset.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store asset NOT_FOUND store assetId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store asset not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        storeAssetsRepository.delete(optStoreAsset.get());
        
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
    @PutMapping(path = {"/{id}"}, name = "store-assets-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-assets-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> putStoreAssetsById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id,
            @RequestParam(name = "assetFile", required = false) MultipartFile uploadedFile
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
        
        Optional<StoreAssets> storeAssetOpt = storeAssetsRepository.findById(id);
        StoreAssets storeAsset = null;
        if (storeAssetOpt.isPresent()) {
            storeAsset = storeAssetOpt.get();
        } else {
            storeAsset = new StoreAssets();
        }
        
        if (null != uploadedFile) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Uploaded Filename: " + uploadedFile.getOriginalFilename());
            String logoStoragePath = fileStorageService.saveStoreAsset(uploadedFile, storeId + "-"+storeAsset.getAssetType());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Asset storagePath: " + logoStoragePath);
            storeAsset.setAssetUrl(storeAssetsBaseUrl + storeId + "-"+storeAsset.getAssetType());
        } 
        
        response.setStatus(HttpStatus.OK);
        response.setData(storeAssetsRepository.save(storeAsset));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
    private List<StoreAssets> SetDefaultAsset(Store storeInfo, String storeId, List<StoreAssets> storeAssetsList) {
        List<StoreAssets> desktopBannerList = storeAssetsRepository.findByStoreIdAndAssetType(storeId, StoreAssetType.BannerDesktopUrl);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "SetDefaultAsset", "desktopBannerList found:"+desktopBannerList.toString());
        if (desktopBannerList.isEmpty()) {
            StoreAssets defaultBanner = new StoreAssets();
            if (storeInfo.getVerticalCode()!=null) {                
                if (storeInfo.getVerticalCode().toUpperCase().contains("FNB")) {
                    defaultBanner.setAssetUrl(storeBannerFnbDefaultUrl);                
                } else {
                    defaultBanner.setAssetUrl(storeBannerEcommerceDefaultUrl);                
                }
            } else {
                defaultBanner.setAssetUrl(storeBannerEcommerceDefaultUrl);            
            }
            defaultBanner.setAssetType(StoreAssetType.BannerDesktopUrl);
            defaultBanner.setStoreId(storeId);
            storeAssetsList.add(defaultBanner);
        }
        
        List<StoreAssets> mobileBannerList = storeAssetsRepository.findByStoreIdAndAssetType(storeId, StoreAssetType.BannerMobileUrl);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "SetDefaultAsset", "mobileBannerList found:"+mobileBannerList.toString());        
        if (mobileBannerList.isEmpty()) {
            StoreAssets defaultBanner = new StoreAssets();
            if (storeInfo.getVerticalCode()!=null) {                
                if (storeInfo.getVerticalCode().toUpperCase().contains("FNB")) {
                    defaultBanner.setAssetUrl(storeBannerFnbDefaultUrl);                
                } else {
                    defaultBanner.setAssetUrl(storeBannerEcommerceDefaultUrl);                
                }
            } else {
                defaultBanner.setAssetUrl(storeBannerEcommerceDefaultUrl);            
            }
            defaultBanner.setAssetType(StoreAssetType.BannerMobileUrl);
            defaultBanner.setStoreId(storeId);
            storeAssetsList.add(defaultBanner);
        }
        
        List<StoreAssets> logoList = storeAssetsRepository.findByStoreIdAndAssetType(storeId, StoreAssetType.LogoUrl);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "SetDefaultAsset", "logoList found:"+logoList.toString());        
        if (logoList.isEmpty()) {
            StoreAssets defaultLogo = new StoreAssets();
            defaultLogo.setAssetUrl(storeLogoDefaultUrl);                        
            defaultLogo.setAssetType(StoreAssetType.LogoUrl);
            defaultLogo.setStoreId(storeId);
            storeAssetsList.add(defaultLogo);
        }
        
        return storeAssetsList;
    }

}
