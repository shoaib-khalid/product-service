package com.kalsym.product.service.controller;

import com.kalsym.product.service.Main;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.Product;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.model.repository.ProductRepository;
import com.kalsym.product.service.utility.DateTimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/product")
public class ProductController {

    private static Logger logger = LoggerFactory.getLogger("application");

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {"", "/{productId}"}, name = "product-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('product-get', 'all')")
    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
    public ResponseEntity<HttpResponse> getProduct(HttpServletRequest request,
            @PathVariable(required = false) String productId,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("product-get, storeId: {}, productId: {}", storeId, productId);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        // if productId pathVariable is provided, ignore all other variables
        if (productId != null) {
            Optional<Product> matchedProduct = productRepository.findById(productId);
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(matchedProduct);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        Product productMatch = new Product();

        Pageable pageable = PageRequest.of(page, pageSize);
        productMatch.setStoreId(storeId);
        productMatch.setName(name);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Product> example = Example.of(productMatch, matcher);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @GetMapping(path = {"/{productId}"}, name = "product-get", produces = "application/json", params = {"storeId", "categoryId", "featured"})
//    @PreAuthorize("hasAnyAuthority('product-get', 'all')")
//    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
//    public ResponseEntity<HttpResponse> getProductById(HttpServletRequest request,
//            @RequestParam(required = false) String storeId,
//            @RequestParam(required = false) String categoryId,
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false, defaultValue = "true") boolean featured,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int pageSize) {
//
//        logger.info("product-get, storeId: {}", storeId);
//        HttpResponse response = new HttpResponse(request.getRequestURI());
//
//        Product productMatch = new Product();
//
//        Pageable pageable = PageRequest.of(page, pageSize);
//        productMatch.setStoreId(storeId);
//        productMatch.setName(name);
//        ExampleMatcher matcher = ExampleMatcher
//                .matchingAll()
//                .withIgnoreCase()
//                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
//        Example<Product> example = Example.of(productMatch, matcher);
//
//        response.setSuccessStatus(HttpStatus.OK);
//        response.setData(productRepository.findAll(example, pageable));
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    @PutMapping(path = {"/{storeId}"}, name = "product-put-by-store-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('product-put-by-store-id', 'all')")
    public ResponseEntity<HttpResponse> putProductByStoreId(HttpServletRequest request, @PathVariable String storeId, @RequestBody Product bodyProduct) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info("products-put, storeId: {}", storeId);

        logger.info(Main.VERSION, logprefix, "", "");
        logger.info(Main.VERSION, bodyProduct.toString(), "");

        Optional<Store> storeOpt = storeRepository.findById(storeId);

        if (!storeOpt.isPresent()) {
            logger.info(Main.VERSION, logprefix, "store not found, for id: {}", storeId);
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        logger.info(Main.VERSION, logprefix, "store found for id: {}", storeId);

        //TODO: add product details, options and features as well
        productRepository.save(bodyProduct);
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(productRepository.save(bodyProduct));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     *
     * @param request
     * @param storeId
     * @param name
     * @param featured
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(path = {"/search"}, name = "product-search", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('product-search', 'all')")
    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
    public ResponseEntity<HttpResponse> searchProduct(HttpServletRequest request,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("product-get, storeId: {}", storeId);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Product productMatch = new Product();

        Pageable pageable = PageRequest.of(page, pageSize);
        productMatch.setStoreId(storeId);
        productMatch.setName(name);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Product> example = Example.of(productMatch, matcher);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @DeleteMapping(path = {"/{productId}"}, name = "product-delete-by-id")
    @PreAuthorize("hasAnyAuthority('product-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteProductById(HttpServletRequest request, @PathVariable String productId) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(Main.VERSION, "product-delete-by-id, productId: {}", productId);

        Optional<Product> optProduct = productRepository.findById(productId);

        if (!optProduct.isPresent()) {
            logger.info(Main.VERSION, logprefix, "product not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        logger.info(Main.VERSION, logprefix, "product found", "");
        productRepository.delete(optProduct.get());

        logger.info(Main.VERSION, logprefix, "product deleted, with id: {}", productId);
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "product-put-by-id")
    @PreAuthorize("hasAnyAuthority('product-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putProductById(HttpServletRequest request, @PathVariable String id, @RequestBody Product bodyProduct) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(Main.VERSION, logprefix, "", "");
        logger.info(Main.VERSION, logprefix, bodyProduct.toString(), "");

        Optional<Product> optProduct = productRepository.findById(id);

        if (!optProduct.isPresent()) {
            logger.info(Main.VERSION, logprefix, "product not found with productId: {}", id);
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        logger.info(Main.VERSION, logprefix, "product found with productId: {}", id);
        Product product = optProduct.get();
        List<String> errors = new ArrayList<>();

        product.update(bodyProduct);

        logger.info(Main.VERSION, logprefix, "product updated for productId: " + id, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(productRepository.save(product));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
