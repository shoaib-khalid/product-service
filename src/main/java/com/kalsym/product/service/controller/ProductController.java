package com.kalsym.product.service.controller;

import com.kalsym.product.service.model.HttpResponse;
import com.kalsym.product.service.model.Product;
import com.kalsym.product.service.model.ProductRepository;
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
//import org.springframework.security.access.prepost.PreAuthorize;
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

    //@GetMapping(path = {"/{id}"}, name = "products-get-by-storeId")
    //@PreAuthorize("hasAnyAuthority('products-get-by-storeId', 'all')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId"})
    public ResponseEntity<HttpResponse> getProductsByStoreId(HttpServletRequest request, @RequestParam("storeId") String storeId) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info("products-get-by-storeId, storeId: {}", storeId);
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productRepository.findByStoreId(storeId));

        logger.info("role found", "");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
