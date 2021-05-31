package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.repository.ProductRepository;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.model.repository.StoreCategoryRepository;
import com.kalsym.product.service.utility.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.utility.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping(path = {""}, name = "store-categories-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get', 'all')")
    public ResponseEntity<HttpResponse> getCategory(HttpServletRequest request,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        StoreCategory storeCategory = new StoreCategory();

        storeCategory.setName(name);
        storeCategory.setStoreId(storeId);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<StoreCategory> example = Example.of(storeCategory, matcher);

        Pageable pageable = PageRequest.of(page, pageSize);

        response.setStatus(HttpStatus.OK);
        response.setData(storeCategoryRepository.findAll(example, pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "store-categories-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-post','all')")
    public ResponseEntity<HttpResponse> postStoreCategoryByStoreId(HttpServletRequest request,
            @Valid @RequestBody StoreCategory bodyStoreCategory) throws Exception {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        //TODO: implement check for userId authentication with storeId, so only owner of his own store can create a category of store
        Optional<Store> store = storeRepository.findById(bodyStoreCategory.getStoreId());

        if (store == null) {
            Logger.application.error("store doesn't exist with id: {}", bodyStoreCategory.getId());

            response.setStatus(HttpStatus.FAILED_DEPENDENCY);
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(response);
        }

        List<String> errors = new ArrayList<>();

        storeCategoryRepository.save(bodyStoreCategory);

        Logger.application.info("storeCategory created with id: {}", bodyStoreCategory.getId());
        response.setStatus(HttpStatus.CREATED);
        response.setData(storeCategoryRepository.save(bodyStoreCategory));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(path = {"/{storeCategoryId}"}, name = "store-categories-delete-by-id")
    @PreAuthorize("hasAnyAuthority('store-categories-delete-by-id', 'all')")
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

}
