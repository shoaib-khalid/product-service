package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.Product;
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
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

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

//    /**
//     *
//     * @param request
//     * @param storeId
//     * @param productId
//     * @param page
//     * @param pageSize
//     * @return
//     * @deprecated use ProductController.getProduct instead (GET:
//     * product?storeId=xyz)
//     */
//    @GetMapping(path = {"/{storeId}/products"}, name = "products-get-by-store", produces = "application/json")
//    @PreAuthorize("hasAnyAuthority('products-get-by-store','all')")
//    public ResponseEntity<HttpResponse> getProductByStore(HttpServletRequest request,
//            @PathVariable(required = true) String storeId,
//            @RequestParam(required = false) String productId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int pageSize) {
//        String logprefix = request.getRequestURI();
//        HttpResponse response = new HttpResponse(request.getRequestURI());
//
//        Pageable pageable = PageRequest.of(page, pageSize);
//
//        Logger.application.info("products-get-by-store, storeId: {}, productId: {}", storeId, productId);
//        Product productMatch = new Product();
//        productMatch.setId(productId);
//        productMatch.setStoreId(storeId);
//        ExampleMatcher matcher = ExampleMatcher
//                .matchingAll()
//                .withIgnoreCase()
//                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
//        Example<Product> example = Example.of(productMatch, matcher);
//
//        response.setSuccessStatus(HttpStatus.OK);
//        response.setData(productRepository.findAll(example, pageable));
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//
//    }

    @PostMapping(path = {""}, name = "stores-post")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postStore(HttpServletRequest request, @Valid @RequestBody Store bodyStore) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
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

    @PostMapping(path = {"/{storeId}/products"}, name = "products-post-by-store")
    @PreAuthorize("hasAnyAuthority('products-post-by-store', 'all')")
    public ResponseEntity<HttpResponse> postProductByStore(HttpServletRequest request, @PathVariable String storeId, @Valid @RequestBody Product bodyProduct) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, bodyProduct.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        Product savedProduct = productRepository.save(bodyProduct);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product added to store with storeId: {}, productId: {}" + storeId, savedProduct.getId());
        response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = {"/{storeId}"}, name = "products-put-by-stores-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-put-by-stores-id', 'all')")
    public ResponseEntity<HttpResponse> putProductByStoreId(HttpServletRequest request, @PathVariable String storeId, @RequestBody Product bodyProduct) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info("products-put, storeId: {}", storeId);

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(ProductServiceApplication.VERSION, bodyProduct.toString(), "");

        Optional<Store> storeOpt = storeRepository.findById(storeId);

        if (!storeOpt.isPresent()) {
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "store not found, for id: {}", storeId);
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "store found for id: {}", storeId);

        //TODO: add product details, options and features as well
        productRepository.save(bodyProduct);
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(productRepository.save(bodyProduct));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
