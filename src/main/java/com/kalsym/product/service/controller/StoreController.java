package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.StoreCategory;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.repository.ProductRepository;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.utility.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
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
import org.springframework.web.bind.annotation.RequestParam;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.product.*;
import com.kalsym.product.service.model.repository.StoreCategoryRepository;
import com.kalsym.product.service.utility.Logger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7cu
 *
 * GET /stores GET /stores/{id}
 *
 * POST /stores
 *
 * DELETE /stores/{id}
 *
 * PUT /stores/{id}
 *
 *
 *
 *
 * GET /stores/{storeId}/products
 *
 * POST /stores/{storeId}/products
 *
 * GET /stores/{storeId}/store-categories
 *
 * POST /stores/{storeId}/store-categories
 */
@RestController()
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @GetMapping(path = {""}, name = "stores-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-get', 'all')")
    public ResponseEntity<HttpResponse> getStore(HttpServletRequest request,
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String verticalCode,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        Store store = new Store();

        store.setClientId(clientId);
        store.setCity(city);
        store.setName(name);
        store.setVerticalCode(verticalCode);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Store> example = Example.of(store, matcher);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);
        response.setData(storeRepository.findAll(example, pageable));
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "stores-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<Store> optStore = storeRepository.findById(id);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        response.setData(optStore.get());
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {""}, name = "stores-post")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postStore(HttpServletRequest request,
            @Valid @RequestBody Store bodyStore) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "stores-post", "");
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, bodyStore.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        Store savedStore = null;
        try {
            savedStore = storeRepository.save(bodyStore);
        } catch (Exception exp) {
            Logger.application.error("Error in creating store", exp);
            response.setMessage(exp.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "store created with id: " + savedStore.getId());
        response.setData(savedStore);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "stores-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id,
            @Valid @RequestBody Store bodyStore
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<Store> optStore = storeRepository.findById(id);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);

        Store store = optStore.get();

        store.update(bodyStore);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "updated store with id: " + id);
        response.setData(storeRepository.save(store));
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "stores-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<Store> optStore = storeRepository.findById(id);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setSuccessStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        storeRepository.deleteById(id);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleted store with id: " + id);
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    

    @PostMapping(path = {"/{storeId}/store-categories"}, name = "store-categories-post-by-store-id")
    @PreAuthorize("hasAnyAuthority('store-categories-post-by-store-id', 'all')")
    public ResponseEntity<HttpResponse> postStoreCategoryByStoreId(HttpServletRequest request,
            @PathVariable String storeId,
            @Valid @RequestBody StoreCategory bodyStoreCategory) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, bodyStoreCategory.toString(), "");

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setSuccessStatus(HttpStatus.NOT_FOUND, "store not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        bodyStoreCategory.setStoreId(storeId);

        StoreCategory savedStoreCategory = storeCategoryRepository.save(bodyStoreCategory);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product added to store with storeId: {}, productId: {}" + storeId, savedStoreCategory.getId());
        response.setSuccessStatus(HttpStatus.CREATED);

        response.setData(savedStoreCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = {"/{storeId}/store-categories"}, name = "store-categories-get-by-stores-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get-by-stores-id', 'all')")
    public ResponseEntity<HttpResponse> putStoreCategoryByStoreId(HttpServletRequest request,
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

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "store found for id: {}", storeId);

        List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(storeId);
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(storeCategories);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
