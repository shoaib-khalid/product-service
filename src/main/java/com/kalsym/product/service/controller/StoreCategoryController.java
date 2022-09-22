package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.StoreCategoryRepository;
import com.kalsym.product.service.utility.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.service.CloneProductService;
import com.kalsym.product.service.service.FileStorageService;
import com.kalsym.product.service.utility.Logger;
import com.kalsym.product.service.worker.BulkDeleteCategory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/store-categories")
public class StoreCategoryController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    CloneProductService cloneProductService;

    @Value("${store.assets.url:https://symplified.ai/store-assets}")
    private String storeAssetsBaseUrl;

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    @GetMapping(path = {""}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getCategory(HttpServletRequest request,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String verticalCode,
            @RequestParam(required = false) String parentCategoryId,
            @RequestParam(required = false, defaultValue = "name") String sortByCol,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        StoreCategory storeCategory = new StoreCategory();

        storeCategory.setName(name);
        storeCategory.setStoreId(storeId);
        storeCategory.setVerticalCode(verticalCode);
        storeCategory.setParentCategoryId(parentCategoryId);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("parentCategoryId", new GenericPropertyMatcher().exact())
                .withMatcher("verticalCode", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<StoreCategory> example = Example.of(storeCategory, matcher);
        
        Pageable pageable = PageRequest.of(page, pageSize);
        if (sortingOrder==Sort.Direction.ASC)
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        else if (sortingOrder==Sort.Direction.DESC)
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        
        Page<StoreCategory> showData = storeCategoryRepository.findAll(example, pageable);
                
        //to concat store asset url for response data
        for (StoreCategory lc : showData.getContent()){
            //handle null
            if(lc.getThumbnailUrl() != null){
                lc.setThumbnailUrl(assetServiceUrl+lc.getThumbnailUrl());
            }else{
                lc.setThumbnailUrl(null);
            }
        }

        response.setStatus(HttpStatus.OK);
        response.setData(showData);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "store-categories-post")
    @PreAuthorize("hasAnyAuthority('store-categories-post','all')  and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreCategoryByStoreId(HttpServletRequest request,
            @RequestParam() String name,
            @RequestParam(required = false) String parentCategoryId, 
            @RequestParam(required = false) Integer displaySequence, 
            @RequestParam() String storeId, 
            @RequestParam(name = "file", required = false) MultipartFile file) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

//        //TODO: implement check for userId authentication with storeId, so only owner of his own store can create a category of store
        Optional<Store> store = storeRepository.findById(storeId);
        if (!store.isPresent()) {
            Logger.application.error("store doesn't exist with id: {}", storeId);
            response.setStatus(HttpStatus.FAILED_DEPENDENCY);
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(response);
        }

        List<StoreCategory> storeCategoryNames = storeCategoryRepository.findByNameAndStoreId(name, storeId);
        List<String> errors = new ArrayList<>();
        if (storeCategoryNames.size() > 0) {
            Logger.application.error("store doesn't exist with id: {}", storeId);
            response.setStatus(HttpStatus.CONFLICT);
            errors.add("Category already exists");
            response.setData(errors);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        StoreCategory bodyStoreCategory = new StoreCategory();
        bodyStoreCategory.setName(name);
        bodyStoreCategory.setParentCategoryId(parentCategoryId);
        bodyStoreCategory.setDisplaySequence(displaySequence);
        bodyStoreCategory.setStoreId(storeId);
        storeCategoryRepository.save(bodyStoreCategory);
        if (file != null) {
            Logger.application.info("storeCategory created with id: {}", bodyStoreCategory.getId());
            String categoryThumbnailStoragePath = fileStorageService.saveStoreAsset(file, bodyStoreCategory.getId() + fileStorageService.getFileExtension(file));
            bodyStoreCategory.setThumbnailUrl("/store-assets/"+ bodyStoreCategory.getId() + fileStorageService.getFileExtension(file));
        }

        StoreCategory saveAssetUrl = storeCategoryRepository.save(bodyStoreCategory);
        //to concat store asset url for response data
        //handle null
        if(saveAssetUrl.getThumbnailUrl() != null){
            saveAssetUrl.setThumbnailUrl(assetServiceUrl+saveAssetUrl.getThumbnailUrl());

        } else{
            saveAssetUrl.setThumbnailUrl(null);

        }

        response.setStatus(HttpStatus.CREATED);
        response.setData(saveAssetUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(path = {"/{storeCategoryId}"}, name = "store-categories-delete-by-id")
    @PreAuthorize("hasAnyAuthority('store-categories-delete-by-id', 'all')  and @customOwnerVerifier.VerifyStoreCategory(#storeCategoryId)")
    public ResponseEntity<HttpResponse> deleteStoreCategoryById(HttpServletRequest request, @PathVariable String storeCategoryId) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, "store-categories-delete-by-id, categoryId: {}", storeCategoryId);

        Optional<StoreCategory> optStoreCategory = storeCategoryRepository.findById(storeCategoryId);

        if (!optStoreCategory.isPresent()) {
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeCategory not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeCategory found", "");
        storeCategoryRepository.delete(optStoreCategory.get());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeCategory deleted, with id: {}", storeCategoryId);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"{storeCategoryId}"}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getCategoryById(HttpServletRequest request,
            @PathVariable String storeCategoryId) {

        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        Optional<StoreCategory> optStoreCategory = storeCategoryRepository.findById(storeCategoryId);
        if (!optStoreCategory.isPresent()) {
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeCategory not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "Store category found with id: {}", storeCategoryId);

        //to concat store asset url for response data
        // handle null
        if(optStoreCategory.get().getThumbnailUrl() !=null){
            optStoreCategory.get().setThumbnailUrl(assetServiceUrl+optStoreCategory.get().getThumbnailUrl());

        } else{
            optStoreCategory.get().setThumbnailUrl(null);

        }

        response.setData(optStoreCategory);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{storeCategoryId}"}, name = "store-product-assets-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-product-assets-put-by-id', 'all') and @customOwnerVerifier.VerifyStoreCategory(#storeCategoryId)")
    public ResponseEntity<HttpResponse> putStoreProductAssetsById(HttpServletRequest request,
            @PathVariable String storeCategoryId,
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "parentCategoryId" ,required = false) String parentCategoryId, 
            @RequestParam(name = "displaySequence", required = false) Integer displaySequence,
            @RequestParam(name = "storeId", required = true) String storeId,
            @RequestParam(name = "file", required = false) MultipartFile file) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Optional<StoreCategory> optStoreCategory = storeCategoryRepository.findById(storeCategoryId);

        if (!optStoreCategory.isPresent()) {
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeCategory not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

// Code below check if category name already exists
//        List<StoreCategory> storeCategoryNames = storeCategoryRepository.findByNameAndStoreId(name, storeId);
//        List<String> errors = new ArrayList<>();
//        if (storeCategoryNames.size() > 0) {
//            Logger.application.error("store doesn't exist with id: {}", storeId);
//            response.setStatus(HttpStatus.CONFLICT);
//            errors.add("Category already exists");
//            response.setData(errors);
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//        }
        if (name != null) {
            optStoreCategory.get().setName(name);
        }

        if (parentCategoryId != null) {
            optStoreCategory.get().setParentCategoryId(parentCategoryId);
        }
        
        if (displaySequence != null) {
            optStoreCategory.get().setDisplaySequence(displaySequence);
        }

        if (file != null) {
            String categoryThumbnailStoragePath = fileStorageService.saveStoreAsset(file, optStoreCategory.get().getId() + fileStorageService.getFileExtension(file));
            optStoreCategory.get().setThumbnailUrl("/store-assets/" + optStoreCategory.get().getId() + fileStorageService.getFileExtension(file));
        }

        response.setStatus(HttpStatus.OK);
        storeCategoryRepository.save(optStoreCategory.get());
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @PostMapping(path = {"/bulk-delete"}, name = "store-products-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-products-delete-by-id', 'all')  and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStoreCategoryByBulk(HttpServletRequest request,
            @RequestBody List<String> categoryIds) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        //create thread
        BulkDeleteCategory threadBulkDeleteCategory = new BulkDeleteCategory(categoryIds,cloneProductService);
        threadBulkDeleteCategory.start();

        response.setStatus(HttpStatus.OK);
        response.setData("Success Deleted");
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
