package com.kalsym.product.service.controller;

import com.kalsym.product.service.Main;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.Product;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.model.repository.ProductRepository;
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

    @GetMapping(path = {""}, name = "product-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('product-get', 'all')")
    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
    public ResponseEntity<HttpResponse> getProductsByStoreId(HttpServletRequest request,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);
        if (storeId != null && name != null) {
            logger.info("products-get, storeId: {}, name: {}", storeId, name);
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(productRepository.findByStoreIdAndName(storeId, name));
        } else if (storeId != null && featured) {
            logger.info("products-get, storeId: {}", storeId);
            response.setSuccessStatus(HttpStatus.OK);
            response.setData(productRepository.findByStoreId(storeId));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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
            logger.info(Main.VERSION, logprefix, "store not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        logger.info(Main.VERSION, logprefix, "store found", "");
  
        logger.info(Main.VERSION, logprefix, "product updated for storeId: " + storeId);
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(productRepository.save(bodyProduct));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
