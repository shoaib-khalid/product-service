package com.kalsym.product.service.controller;

import com.kalsym.product.service.Main;
import com.kalsym.product.service.model.Product;
import com.kalsym.product.service.model.repository.ProductRepository;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.utility.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/store")
public class StoreController {

    private static Logger logger = LoggerFactory.getLogger("application");

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""}, name = "store-get", produces = "application/json", params = {"clientId"})
    @PreAuthorize("hasAnyAuthority('store-get', 'all')")
    public ResponseEntity<HttpResponse> getStore(HttpServletRequest request,
            @RequestParam(required = false) String clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);
        if (clientId == null) {
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(storeRepository.findAll(pageable));
        } else {
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(storeRepository.findByClientId(clientId, pageable));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     *
     * @param request
     * @param storeId
     * @param productId
     * @param page
     * @param pageSize
     * @return
     * @deprecated use ProductController.getProduct instead
     */
    @GetMapping(path = {"/{storeId}/product"}, name = "product-get-by-store", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('product-get-by-store','all')")
    public ResponseEntity<HttpResponse> getProductByStore(HttpServletRequest request,
            @PathVariable(required = true) String storeId,
            @RequestParam(required = false) String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);

        logger.info("product-get-by-store, storeId: {}, productId: {}", storeId, productId);
        Product productMatch = new Product();
        productMatch.setId(productId);
        productMatch.setStoreId(storeId);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Product> example = Example.of(productMatch, matcher);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping(path = {""}, name = "store-post")
    @PreAuthorize("hasAnyAuthority('store-post', 'all')")
    public ResponseEntity<HttpResponse> postStore(HttpServletRequest request, @Valid @RequestBody Store bodyStore) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(Main.VERSION, logprefix, "", "");
        logger.info(Main.VERSION, logprefix, bodyStore.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        Store savedStore = storeRepository.save(bodyStore);
        logger.info(Main.VERSION, logprefix, "store created with id: " + savedStore.getId());
        response.setData(savedStore);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
