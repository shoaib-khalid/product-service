package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductSpecs;
import com.kalsym.product.service.model.product.ProductWithDetails;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.ProductAssetRepository;
import com.kalsym.product.service.repository.ProductInventoryItemRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.ProductVariantRepository;
import com.kalsym.product.service.repository.ProductVariantAvailableRepository;
import com.kalsym.product.service.repository.ProductReviewRepository;
import com.kalsym.product.service.repository.ProductWithDetailsRepository;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.repository.ProductInventoryWithDetailsRepository;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/products")
public class StoreProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductWithDetailsRepository productWithDetailsRepository;

    @Autowired
    ProductInventoryWithDetailsRepository productInventoryRepository;

    @Autowired
    ProductInventoryItemRepository productInventoryItemRepository;

    @Autowired
    ProductVariantRepository productVariantRepository;

    @Autowired
    ProductVariantAvailableRepository productVariantAvailableRepository;

    @Autowired
    ProductReviewRepository productReviewRepository;

    @Autowired
    ProductAssetRepository productAssetRepository;

    @Autowired
    StoreRepository storeRepository;

    @Value("${product.seo.url:https://{{store-domain}}.symplified.store/products/name/{{product-name}}}")
    private String productSeoUrl;

    @GetMapping(path = {""}, name = "store-products-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-products-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreProducts(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String seoName,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false, defaultValue = "name") String sortByCol,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        ProductWithDetails productMatch = new ProductWithDetails();

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Sort By:" + sortingOrder);

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortingOrder, sortByCol));

        if (sortByCol.equalsIgnoreCase("price")) {
            pageable = PageRequest.of(page, pageSize, Sort.by(sortingOrder, "productInventories.price"));
        }

        productMatch.setStoreId(storeId);
        productMatch.setCategoryId(categoryId);
        productMatch.setName(name);
        //productMatch.setStatus("ACTIVE");
        productMatch.setSeoName(seoName);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductWithDetails> example = Example.of(productMatch, matcher);

        //ProductSpecs.getProductsSpec(status, example);
        //List<ProductWithDetails> products = productWithDetailsRepository.findByStoreId(storeId);
        response.setStatus(HttpStatus.OK);

        response.setData(productWithDetailsRepository.findAll(ProductSpecs.getProductsSpec(status, example), pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "store-products-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-products-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreProductById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<ProductWithDetails> optProdcut = productWithDetailsRepository.findById(id);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product FOUND storeId: " + id);

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "store found for id: {}", storeId);

        response.setStatus(HttpStatus.OK);
        response.setData(optProdcut.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-products-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-products-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteStoreProductById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(id);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product FOUND storeId: " + id);

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "store found for id: {}", storeId);

        Product p = optProdcut.get();
        p.setStatus("DELETED");
        productRepository.save(p);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "store-products-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-products-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putStoreProductById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id,
            @Valid @RequestBody Product bodyProduct) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(id);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product NOT_FOUND storeId: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product FOUND storeId: " + id);

        Product product = optProdcut.get();
        product.update(bodyProduct);

        response.setStatus(HttpStatus.OK);
        response.setData(productRepository.save(product));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "store-products-post")
    @PreAuthorize("hasAnyAuthority('store-products-post', 'all')")
    public ResponseEntity<HttpResponse> postStoreProduct(HttpServletRequest request,
            @PathVariable String storeId,
            @Valid @RequestBody Product bodyProduct) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "bodyProduct: " + bodyProduct.toString());

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        List<String> errors = new ArrayList<>();
        List<Product> products = productRepository.findByStoreId(storeId);

        for (Product existingProduct : products) {

            if (existingProduct.getName().equals(bodyProduct.getName()) && !"DELETED".equalsIgnoreCase(existingProduct.getStatus())) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "username already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("Product name already exists");
                response.setData(errors);
                return ResponseEntity.status(response.getStatus()).body(response);
            }

        }

        String seoName = generateSeoName(bodyProduct.getName());

        String seoUrl = productSeoUrl.replace("{{store-domain}}", optStore.get().getDomain());
        seoUrl = seoUrl.replace("{{product-name}}", seoName);
        bodyProduct.setSeoUrl(seoUrl);

        bodyProduct.setSeoName(seoName);
        Product savedProduct = productRepository.save(bodyProduct);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product added to store with storeId: {}, productId: {}" + storeId, savedProduct.getId());

        response.setStatus(HttpStatus.CREATED);
        response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String generateSeoName(String name) throws MalformedURLException {
        name = name.replace(" ", "-");
        name = name.replace("\"", "");
        name = name.replace("'", "");
        name = name.replace("/", "");
        name = name.replace("\\", "");
        name = name.replace("&", "");
        name = name.replace(",", "");
        name = name.replace("%", "percent");

        name = name.replace("(", "%28");
        name = name.replace(")", "%29");
        return name;
    }

}
