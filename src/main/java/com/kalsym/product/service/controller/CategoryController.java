package com.kalsym.product.service.controller;

import com.kalsym.product.service.Main;
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
import com.kalsym.product.service.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
    public ResponseEntity<HttpResponse> getCategory(HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("category-get");
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productRepository.findAll(pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {""}, name = "category-get-by-store-id", produces = "application/json", params={"storeId"})
    @PreAuthorize("hasAnyAuthority('category-get-by-store-id', 'all')")
    public ResponseEntity<HttpResponse> getCategoryByStoreId(HttpServletRequest request,
            @RequestParam(required = true) String storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);

        logger.info("category-get-by-store-id, storeId: {}", storeId);

        Category categoryMatch = new Category();

        categoryMatch.setStoreId(storeId);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Category> exampleCategory = Example.of(categoryMatch, matcher);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(categoryRepository.findAll(exampleCategory, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{categoryId}/product/{productId}", "/{categoryId}/product"}, name = "product-get-by-category", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('product-get-by-category','all')")
    public ResponseEntity<HttpResponse> getProductByCategoryId(HttpServletRequest request,
            @PathVariable(required = true) String categoryId,
            @RequestParam(required = false) String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);

        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setId(productId);
        ExampleMatcher matcherProduct = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Product> exampleProduct = Example.of(product, matcherProduct);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productRepository.findAll(exampleProduct, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping(path = {""}, name = "category-post-by-store", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('category-post-by-store','all')")
    public ResponseEntity<HttpResponse> postCategoryByStoreId(HttpServletRequest request, @Valid @RequestBody Category bodyCategory) throws Exception {
        //List<Store> stores = storeRepository.findAll();
        List<String> errors = new ArrayList<>();

        HttpResponse response = new HttpResponse(request.getRequestURI());

        categoryRepository.save(bodyCategory);

        logger.info(Main.VERSION, "category created with id: {}", bodyCategory.getId());
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(categoryRepository.save(bodyCategory));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping(path = {"/{categoryId}"}, name = "category-delete-by-id")
    @PreAuthorize("hasAnyAuthority('category-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteCategoryById(HttpServletRequest request, @PathVariable String categoryId) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(Main.VERSION, "category-delete-by-id, categoryId: {}", categoryId);

        Optional<Category> optCategory = categoryRepository.findById(categoryId);

        if (!optCategory.isPresent()) {
            logger.info(Main.VERSION, logprefix, "category not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        logger.info(Main.VERSION, logprefix, "category found", "");
        categoryRepository.delete(optCategory.get());

        logger.info(Main.VERSION, logprefix, "category deleted, with id: {}", categoryId);
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    

}
