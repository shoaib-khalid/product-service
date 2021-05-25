package com.kalsym.product.service.controller;

import com.kalsym.product.service.service.StoreSubdomainHandler;
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.StoreCategory;
import com.kalsym.product.service.model.repository.ProductRepository;
import com.kalsym.product.service.model.repository.StoreRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.StoreWithDetails;
import com.kalsym.product.service.model.livechatgroup.StoreCreationResponse;
import com.kalsym.product.service.model.repository.StoreCategoryRepository;
import com.kalsym.product.service.model.repository.StoreWithDetailsRepository;
import com.kalsym.product.service.service.StoreLiveChatService;
import com.kalsym.product.service.utility.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
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
    StoreWithDetailsRepository storeWithDetailsRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Autowired
    StoreSubdomainHandler storeSubdomainHandler;

    @Autowired
    StoreLiveChatService storeLiveChatService;

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

        try {
            StoreWithDetails store = new StoreWithDetails();

            store.setClientId(clientId);
            store.setCity(city);
            store.setName(name);
            store.setVerticalCode(verticalCode);
            store.setRegionCountry(null);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store: " + store, "");

            ExampleMatcher matcher = ExampleMatcher
                    .matchingAll()
                    .withIgnoreCase()
                    .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
            Example<StoreWithDetails> example = Example.of(store, matcher);

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<StoreWithDetails> fetchedPage = storeWithDetailsRepository.findAll(example, pageable);
            //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "elements: " + fetchedPage.getTotalElements(), "");
            //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "element 0: " + fetchedPage.iterator().next(), "");

            response.setData(fetchedPage);
            response.setSuccessStatus(HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Error fetching stores", "", e);

            response.setErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @GetMapping(path = {"/{id}"}, name = "stores-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<StoreWithDetails> optStore = storeWithDetailsRepository.findById(id);

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

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store: " + bodyStore.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        Store savedStore = null;
        List<Store> stores = storeRepository.findAll();

        List<String> errors = new ArrayList<>();

        for (Store store : stores) {
            if (store.getName().equals(bodyStore.getName())) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store name already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("store name already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (store.getDomain() != null && store.getDomain().equals(bodyStore.getDomain())) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store domain already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("store domain already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        }

        try {

            //temp fix to remove apostrophy
            String storeDomain = bodyStore.getName().replace("'", "");
            storeDomain = storeDomain.replace(" ", "-").toLowerCase();
            bodyStore.setDomain(storeDomain);
            String domain = storeSubdomainHandler.createSubDomain(bodyStore.getDomain());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "domain: " + domain, "");

            if (domain != null) {
                bodyStore.setDomain(domain);
                savedStore = storeRepository.save(bodyStore);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store created with id: " + savedStore.getId(), "");
            } else {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "domain could not be created", "");
                response.setSuccessStatus(HttpStatus.INTERNAL_SERVER_ERROR, "domain could not be created");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            StoreCreationResponse scrCsr = storeLiveChatService.createGroup(domain + "-csr");

            if (scrCsr == null) {
                storeRepository.delete(savedStore);
                Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group could not be created", "");
                response.setSuccessStatus(HttpStatus.INTERNAL_SERVER_ERROR, "store group could nto be created");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } else {
                savedStore.setLiveChatCsrGroupId(scrCsr.get_id());
                savedStore.setLiveChatCsrGroupName(domain + "-csr");
                storeRepository.save(savedStore);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group created", "");

            }

            StoreCreationResponse scrOrders = storeLiveChatService.createGroup(domain + "-orders");

            if (scrOrders == null) {
                storeLiveChatService.deleteGroup(scrCsr.get_id());
                storeRepository.delete(savedStore);
                Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group could not be created", "");
                response.setSuccessStatus(HttpStatus.INTERNAL_SERVER_ERROR, "store group could nto be created");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } else {
                savedStore.setLiveChatCsrGroupId(scrOrders.get_id());
                savedStore.setLiveChatCsrGroupName(domain + "-orders");
                storeRepository.save(savedStore);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group created", "");

            }

            response.setData(savedStore);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " error creating store ", "", e);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PutMapping(path = {"/{id}"}, name = "stores-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id,
            @Valid @RequestBody Store bodyStore
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

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
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "error saving store " + id, e);
            //response.setData(storeRepository.save(store));
            response.setErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

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

        storeLiveChatService.deleteGroup(optStore.get().getLiveChatCsrGroupId());
        storeLiveChatService.deleteGroup(optStore.get().getLiveChatOrdersGroupId());

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
