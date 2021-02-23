package com.kalsym.product.service.controller;

import com.kalsym.product.service.Main;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.repository.CategoryRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.model.Category;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/category")
public class CategoryController {

    private static Logger logger = LoggerFactory.getLogger("application");

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping(path = {""}, name = "category-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('category-get', 'all')")
    public ResponseEntity<HttpResponse> getCategory(HttpServletRequest request,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);
        if (storeId != null && userId != null) {
            logger.info("products-get, storeId: {}, userId: {}", storeId, userId);
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(storeRepository.findByStoreIdAndName(storeId, name));
        }
        if (storeId == null && name == null) {
            logger.info("products-get, storeId: {}, name: {}", storeId, name);
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(storeRepository.findAll(pageable));
        } else if (storeId != null && name != null) {
            logger.info("products-get, storeId: {}, name: {}", storeId, name);
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(storeRepository.findByStoreIdAndName(storeId, name));
        } else if (storeId != null) {
            logger.info("products-get, storeId: {}", storeId);
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(storeRepository.findByStoreId(storeId));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{categoryId}/product/{productId}"}, name = "product-get-by-category", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('product-get-by-category','all')")
    public ResponseEntity<HttpResponse> getProduct(HttpServletRequest request,
            @PathVariable(required = true) String categoryId,
            @RequestParam(required = false) String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
//        Example<Role> example = Example.of(role, matcher);
//
//        if (storeId != null && productId != null) {
//            logger.info("products-get, storeId: {}, productId: {}", storeId, productId);
//            response.setSuccessStatus(HttpStatus.OK);
//            response.setData(productRepository.findByStoreId(storeId));
//        } else if (productId != null) {
//            logger.info("products-get, storeId: {}, productId: {}", storeId, productId);
//            response.setSuccessStatus(HttpStatus.OK);
//            response.setData(productRepository.findById(storeId));
//        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {""}, name = "category-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('category-post','all')")
    public ResponseEntity<HttpResponse> postCategory(HttpServletRequest request, @Valid @RequestBody Category bodyCategory) throws Exception {
        //List<Store> stores = storeRepository.findAll();
        List<String> errors = new ArrayList<>();

        HttpResponse response = new HttpResponse(request.getRequestURI());

        //categoryRepository.save(bodyCategory);
//        for (Store existingStore : stores) {
//            //TODO: not get the storeId from the front-end, or ignore it
//            if (existingStore.getId().equals(bodyCategory.getId())) {
//                logger.info(Main.VERSION, "categoryId already exists", "");
//                response.setErrorStatus(HttpStatus.CONFLICT);
//                errors.add("categoryId already exists");
//                response.setData(errors);
//                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//            }
//        }
        logger.info(Main.VERSION, "category created with id: " + bodyCategory.getId(), "");
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(categoryRepository.save(bodyCategory));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
