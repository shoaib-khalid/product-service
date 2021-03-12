package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.Product;
import com.kalsym.product.service.model.ProductReview;
import com.kalsym.product.service.model.ProductAsset;
import com.kalsym.product.service.model.ProductInventory;
import com.kalsym.product.service.model.ProductInventoryItem;
import com.kalsym.product.service.model.ProductVariant;
import com.kalsym.product.service.model.ProductVariantAvailable;
import com.kalsym.product.service.model.Store;
import com.kalsym.product.service.model.repository.ProductAssetRepository;
import com.kalsym.product.service.model.repository.ProductInventoryRepository;
import com.kalsym.product.service.model.repository.ProductInventoryItemRepository;
import com.kalsym.product.service.model.repository.StoreRepository;
import com.kalsym.product.service.model.repository.ProductRepository;
import com.kalsym.product.service.model.repository.ProductVariantRepository;
import com.kalsym.product.service.model.repository.ProductVariantAvailableRepository;
import com.kalsym.product.service.model.repository.ProductReviewRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/products")
public class ProductController {

    private static Logger logger = LoggerFactory.getLogger("application");

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductInventoryRepository productInventoryRepository;

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

    /**
     * Get product by store or category or productId
     *
     * @param request
     * @param storeId
     * @param categoryId
     * @param featured
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(path = {""}, name = "products-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-get', 'all')")
    public ResponseEntity<HttpResponse> getProduct(HttpServletRequest request,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("products-get, storeId: {}, categoryId: {}", storeId, categoryId);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Product productMatch = new Product();

        Pageable pageable = PageRequest.of(page, pageSize);
        productMatch.setStoreId(storeId);
        productMatch.setCategoryId(categoryId);
        //productMatch.setId(productId);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Product> example = Example.of(productMatch, matcher);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{productId}"}, name = "products-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-get', 'all')")
    public ResponseEntity<HttpResponse> getProduct(HttpServletRequest request,
            @PathVariable(required = false) String productId,
            @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("products-get, productId: {}", productId);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Product productMatch = new Product();

        Pageable pageable = PageRequest.of(page, pageSize);
        productMatch.setId(productId);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Product> example = Example.of(productMatch, matcher);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
    @GetMapping(path = {"/search"}, name = "products-search", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-search', 'all')")
    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
    public ResponseEntity<HttpResponse> searchProduct(HttpServletRequest request,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("products-get, storeId: {}", storeId);
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

    @DeleteMapping(path = {"/{productId}"}, name = "products-delete-by-id")
    @PreAuthorize("hasAnyAuthority('products-delete-by-id', 'all')")
    public ResponseEntity<HttpResponse> deleteProductById(HttpServletRequest request, @PathVariable String productId) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(ProductServiceApplication.VERSION, "products-delete-by-id, productId: {}", productId);

        Optional<Product> optProduct = productRepository.findById(productId);

        if (!optProduct.isPresent()) {
            logger.info(ProductServiceApplication.VERSION, logprefix, "product not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        logger.info(ProductServiceApplication.VERSION, logprefix, "product found", "");
        productRepository.delete(optProduct.get());

        logger.info(ProductServiceApplication.VERSION, logprefix, "product deleted, with id: {}", productId);
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     *
     * @param request
     * @param id
     * @param bodyProduct
     * @return
     */
    @PutMapping(path = {"/{id}"}, name = "products-put-by-id")
    @PreAuthorize("hasAnyAuthority('products-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putProductById(HttpServletRequest request, @PathVariable String id, @RequestBody Product bodyProduct) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(ProductServiceApplication.VERSION, logprefix, "", "");
        logger.info(ProductServiceApplication.VERSION, logprefix, bodyProduct.toString(), "");

        Optional<Product> optProduct = productRepository.findById(id);

        if (!optProduct.isPresent()) {
            logger.info(ProductServiceApplication.VERSION, logprefix, "product not found with productId: {}", id);
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        logger.info(ProductServiceApplication.VERSION, logprefix, "product found with productId: {}", id);
        Product product = optProduct.get();
        List<String> errors = new ArrayList<>();

        product.update(bodyProduct);

        logger.info(ProductServiceApplication.VERSION, logprefix, "product updated for productId: " + id, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(productRepository.save(product));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping(path = {"/{productId}/inventory"}, name = "products-inventory-get-by-product", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-inventory-get-by-product', 'all')")
    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
    public ResponseEntity<HttpResponse> getProductInventoryByProduct(HttpServletRequest request,
            @PathVariable(required = false) String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("products-inventory-get-by-product, productId: {}", productId);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);

        Object matchedProductInventory = productInventoryRepository.findByProductId(productId, pageable);
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(matchedProductInventory);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping(path = {"/{productId}/inventory"}, name = "products-inventory-post-by-product")
    @PreAuthorize("hasAnyAuthority('products-inventory-post-by-product', 'all')")
    public ResponseEntity<HttpResponse> postProductInventoryByProduct(HttpServletRequest request, @PathVariable String productId,
            @Valid @RequestBody ProductInventory bodyProductInventory) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(ProductServiceApplication.VERSION, logprefix, "", "");
        logger.info(ProductServiceApplication.VERSION, logprefix, bodyProductInventory.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        ProductInventory savedProduct = productInventoryRepository.save(bodyProductInventory);
        logger.info(ProductServiceApplication.VERSION, logprefix, "Product Inventory added to product with productId: {}" + productId);
        response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<HttpResponse> deleteProductInventoryByProduct() {
        return null;
    }

    public ResponseEntity<HttpResponse> putProductInventoryByProduct() {
        return null;
    }

    @GetMapping(path = {"/{productId}/variants"}, name = "products-variants-get-by-product-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-variants-get-by-product-id', 'all')")
    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
    public ResponseEntity<HttpResponse> getProductVariantByProduct(HttpServletRequest request,
            @PathVariable(required = false) String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("products-variants-get-by-product, productId: {}", productId);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);

        Object matchedProductVariant = productVariantRepository.findByProductId(productId, pageable);
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(matchedProductVariant);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping(path = {"/{productId}/variants"}, name = "products-variants-post-by-product")
    @PreAuthorize("hasAnyAuthority('products-variants-post-by-product', 'all')")
    public ResponseEntity<HttpResponse> postProductVariantByProduct(HttpServletRequest request, @PathVariable String productId,
            @Valid @RequestBody ProductVariant bodyProductVariant) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(ProductServiceApplication.VERSION, logprefix, "", "");
        logger.info(ProductServiceApplication.VERSION, logprefix, bodyProductVariant.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        ProductVariant savedProduct = productVariantRepository.save(bodyProductVariant);
        logger.info(ProductServiceApplication.VERSION, logprefix, "Product Variant added to product with productId: {}" + productId);
        response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<HttpResponse> deleteProductVariantByProduct() {
        return null;
    }

    public ResponseEntity<HttpResponse> putProductVariantByProduct() {
        return null;
    }

    @GetMapping(path = {"/{productId}/review"}, name = "products-review-get-by-product", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-review-get-by-product', 'all')")
    //@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json", params = {"storeId", "name", "featured"})
    public ResponseEntity<HttpResponse> getProductReviewByProduct(HttpServletRequest request,
            @PathVariable(required = false) String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("products-review-get-by-product, productId: {}", productId);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Pageable pageable = PageRequest.of(page, pageSize);

        Object matchedProductVariant = productReviewRepository.findByProductId(productId, pageable);
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(matchedProductVariant);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {"/{productId}/review"}, name = "products-review-post-by-product")
    @PreAuthorize("hasAnyAuthority('products-review-post-by-product', 'all')")
    public ResponseEntity<HttpResponse> postProductReviewByProduct(HttpServletRequest request, @PathVariable String productId,
            @Valid @RequestBody ProductReview bodyProductReview) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(ProductServiceApplication.VERSION, logprefix, "", "");
        logger.info(ProductServiceApplication.VERSION, logprefix, bodyProductReview.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        ProductReview savedProduct = productReviewRepository.save(bodyProductReview);
        logger.info(ProductServiceApplication.VERSION, logprefix, "Product Variant added to product with productId: {}" + productId);
        response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<HttpResponse> deleteProductReviewByProduct() {
        return null;
    }

    public ResponseEntity<HttpResponse> putProductReviewByProduct() {
        return null;
    }

    @PostMapping(path = {"/{productId}/variants-available"}, name = "products-variants-available-post")
    @PreAuthorize("hasAnyAuthority('products-variants-available-post', 'all')")
    public ResponseEntity<HttpResponse> postProductVariantAvailable(HttpServletRequest request, @PathVariable String productId,
            @Valid @RequestBody ProductVariantAvailable bodyProductVariantAvailable) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(ProductServiceApplication.VERSION, logprefix, "", "");
        logger.info(ProductServiceApplication.VERSION, logprefix, bodyProductVariantAvailable.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        ProductVariantAvailable savedProduct = productVariantAvailableRepository.save(bodyProductVariantAvailable);
        logger.info(ProductServiceApplication.VERSION, logprefix, "Product Variant added to product with productId: {}" + productId);
        response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = {"/{productId}/inventory-item"}, name = "products-inventory-item-post")
    @PreAuthorize("hasAnyAuthority('products-inventory-item-post', 'all')")
    public ResponseEntity<HttpResponse> postProductInventoryItem(HttpServletRequest request, @PathVariable String productId,
            @Valid @RequestBody ProductInventoryItem bodyProductInventoryItem) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(ProductServiceApplication.VERSION, logprefix, "", "");
        logger.info(ProductServiceApplication.VERSION, logprefix, bodyProductInventoryItem.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        ProductInventoryItem savedProduct = productInventoryItemRepository.save(bodyProductInventoryItem);
        logger.info(ProductServiceApplication.VERSION, logprefix, "Product Inventory Item added to product with productId: {}" + productId);
        response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get product assets by productId and/or itemCode
     *
     * @param itemCode
     * @param productId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(path = {"/{productId}/assets"}, name = "products-assets-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-assets-get', 'all')")
    public ResponseEntity<HttpResponse> getProductAssets(HttpServletRequest request,
            @PathVariable(required = true) String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("products-assets-get, productId: {} ", productId);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        ProductAsset productAssetMatch = new ProductAsset();

        Pageable pageable = PageRequest.of(page, pageSize);
        productAssetMatch.setProductId(productId);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductAsset> example = Example.of(productAssetMatch, matcher);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productAssetRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get product assets by productId and/or itemCode
     *
     * @param itemCode
     * @param productId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(path = {"/{productId}/assets/{itemCode}"}, name = "products-assets-get-by-item", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-assets-get-by-item', 'all')")
    public ResponseEntity<HttpResponse> getProductAssetsByItemCode(HttpServletRequest request,
            @PathVariable(required = true) String productId,
            @PathVariable(required = true) String itemCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        logger.info("products-assets-get-by-item, productId: {}, itemCode: {}", productId, itemCode);
        HttpResponse response = new HttpResponse(request.getRequestURI());

        ProductAsset productAssetMatch = new ProductAsset();

        Pageable pageable = PageRequest.of(page, pageSize);
        productAssetMatch.setProductId(productId);
        productAssetMatch.setItemCode(itemCode);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductAsset> example = Example.of(productAssetMatch, matcher);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(productAssetRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {"/{productId}/inventory/assets"}, name = "products-assets-post")
    @PreAuthorize("hasAnyAuthority('products-assets-post', 'all')")
    public ResponseEntity<HttpResponse> postProductAssets(HttpServletRequest request, @PathVariable String productId,
            @Valid @RequestBody ProductAsset bodyProductAsset) throws Exception {

        logger.info("products-assets-post, productId: {}", productId);

        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        logger.info(ProductServiceApplication.VERSION, logprefix, "", "");
        logger.info(ProductServiceApplication.VERSION, logprefix, bodyProductAsset.toString(), "");

        response.setSuccessStatus(HttpStatus.CREATED);
        ProductAsset savedProductAsset = productAssetRepository.save(bodyProductAsset);
        logger.info(ProductServiceApplication.VERSION, logprefix, "Product Asset added to product with productId: {}" + productId);
        response.setData(savedProductAsset);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<HttpResponse> deleteProductAssetById(@PathVariable String productAssetId) {
        logger.info("products-assets-delete, productId: {}", productAssetId);
        return null;
    }

    public ResponseEntity<HttpResponse> putProductAssetById(HttpServletRequest request, @PathVariable String productAssetId) {
        logger.info("products-assets-put, productId: {}", productAssetId);
        return null;
    }
}
