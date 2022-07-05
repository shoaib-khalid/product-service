package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.HashmapLoader;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductAsset;
import com.kalsym.product.service.model.product.ProductInventoryWithDetails;
import com.kalsym.product.service.model.product.ProductSpecs;
import com.kalsym.product.service.model.product.ProductWithDetails;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.ItemDiscount;
import com.kalsym.product.service.model.store.StoreDiscount;
import com.kalsym.product.service.enums.StoreDiscountType;
import com.kalsym.product.service.enums.DiscountCalculationType;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.model.product.ProductInventoryItem;
import com.kalsym.product.service.model.store.StoreDiscountProduct;
import com.kalsym.product.service.model.store.StoreDiscountTier;
import com.kalsym.product.service.model.store.StoreWithDetails;
import com.kalsym.product.service.model.store.object.CustomPageable;
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
import com.kalsym.product.service.repository.RegionCountriesRepository;
import com.kalsym.product.service.repository.StoreDiscountRepository;
import com.kalsym.product.service.repository.StoreDiscountProductRepository;
import com.kalsym.product.service.utility.DateTimeUtil;
import com.kalsym.product.service.utility.ProductDiscount;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.text.SimpleDateFormat; 
import javax.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;

/**
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
    
    @Autowired
    StoreDiscountRepository storeDiscountRepository;
    
    @Autowired
    StoreDiscountProductRepository storeDiscountProductRepository;

    @Autowired
    RegionCountriesRepository regionCountriesRepository;
    
    @Autowired
    private HashmapLoader hashmapLoader;
            
    @Value("${product.seo.url:https://{{store-domain}}/product/{{product-name}}}")
    private String productSeoUrl;

    @Value("${asset.service.url}")
    String assetServiceUrl;

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
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " STATUS: " + status+" categoryId:"+categoryId);

        if (status == null) {
            status = new ArrayList();
            status.add("ACTIVE");
            status.add("INACTIVE");
            status.add("OUTOFSTOCK");
        }

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Sort By:" + sortingOrder);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Sort By column:" + sortByCol);

        Pageable pageable = null;

        if (sortByCol.equals("price")) {
            pageable = PageRequest.of(page, pageSize, sortingOrder, "pi.price");
        } else {
            pageable = PageRequest.of(page, pageSize, sortingOrder, sortByCol);

        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Pageable object created:" + sortingOrder);
        
        
        if (categoryId == null || categoryId.isEmpty()) {
            categoryId = "";
        }
        
        if (name == null || name.isEmpty()) {
            name = "";
        }

        if (seoName == null || seoName.isEmpty()) {
            seoName = "";
        }
        
        /*
        ProductWithDetails productMatch = new ProductWithDetails();
        productMatch.setStoreId(storeId);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductWithDetails> productExample = Example.of(productMatch, matcher);
        */
        
        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }
        
        Page<ProductWithDetails> productWithPage = null;
        if (!categoryId.equals(""))
            productWithPage = productWithDetailsRepository.findByNameOrSeoNameOrCategoryIdAscendingOrderByPrice(storeId, name, seoName, status, categoryId, pageable);        
        else
            productWithPage = productWithDetailsRepository.findByNameOrSeoNameAscendingOrderByPrice(storeId, name, seoName, status, pageable);        
        //Page<ProductWithDetails> productWithPage = productWithDetailsRepository.findAll(getStoreProductSpec(name, seoName, categoryId, status, productExample), pageable);
        List<ProductWithDetails> productList = productWithPage.getContent();
        
        ProductWithDetails[] productWithDetailsList = new ProductWithDetails[productList.size()];
        for (int x=0;x<productList.size();x++) {
            //check for item discount in hashmap
            ProductWithDetails productDetails = productList.get(x);

            //set asset url for List of products asset and thumnailurl
            List<ProductAsset> productAssets = productDetails.getProductAssets();
            for(ProductAsset pa : productAssets){
                pa.setUrl(assetServiceUrl+pa.getUrl());
                //handle null
                if(pa.getUrl() == null){
                    pa.setUrl(null);
                }
            }
            productDetails.setProductAssets(productAssets);
            productDetails.setThumbnailUrl(assetServiceUrl+productDetails.getThumbnailUrl());
            //handle null
            if(productDetails.getThumbnailUrl() == null){
                productDetails.setThumbnailUrl(null);

            }
            
            for (int i=0;i<productDetails.getProductInventories().size();i++) {
                ProductInventoryWithDetails productInventory = productDetails.getProductInventories().get(i);
                //ItemDiscount discountDetails = discountedItemMap.get(productInventory.getItemCode());
                /*ItemDiscount discountDetails = hashmapLoader.GetDiscountedItemMap(storeId, productInventory.getItemCode());*/
                ItemDiscount discountDetails = ProductDiscount.getItemDiscount(storeDiscountRepository, storeId, productInventory.getItemCode(), regionCountry);
                if (discountDetails != null) {                    
                    double discountedPrice = productInventory.getPrice();
                    if (discountDetails.calculationType.equals(DiscountCalculationType.FIX)) {
                        discountedPrice = productInventory.getPrice() - discountDetails.discountAmount;
                    } else if (discountDetails.calculationType.equals(DiscountCalculationType.PERCENT)) {
                        discountedPrice = productInventory.getPrice() - (discountDetails.discountAmount / 100 * productInventory.getPrice());
                    }
                    discountDetails.discountedPrice = discountedPrice;
                    discountDetails.normalPrice = productInventory.getPrice();                    
                    productInventory.setItemDiscount(discountDetails); 
                } else {
                    //get inactive discount if any
                    List<StoreDiscountProduct> discountList = storeDiscountProductRepository.findByItemCode(productInventory.getItemCode());
                    if (!discountList.isEmpty()) {
                        StoreDiscountProduct storeDiscountProduct = discountList.get(0);
                        ItemDiscount inactiveDiscount = new ItemDiscount();
                        inactiveDiscount.discountId = storeDiscountProduct.getStoreDiscountId();
                        productInventory.setItemDiscountInactive(inactiveDiscount);
                    }
                }
            }
            productWithDetailsList[x]=productDetails;
        }
        
        //create custom pageable object with modified content
        CustomPageable customPageable = new CustomPageable();
        customPageable.content = productWithDetailsList;
        customPageable.pageable = productWithPage.getPageable();
        customPageable.totalPages = productWithPage.getTotalPages();
        customPageable.totalElements = productWithPage.getTotalElements();
        customPageable.last = productWithPage.isLast();
        customPageable.size = productWithPage.getSize();
        customPageable.number = productWithPage.getNumber();
        customPageable.sort = productWithPage.getSort();        
        customPageable.numberOfElements = productWithPage.getNumberOfElements();
        customPageable.first  = productWithPage.isFirst();
        customPageable.empty = productWithPage.isEmpty();
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Product Found");
        response.setData(customPageable);
        response.setStatus(HttpStatus.OK);
               
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
        
        RegionCountry regionCountry = null;
        Optional<RegionCountry> t = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
        if (t.isPresent()) {
            regionCountry = t.get();
        }
                
        //check for item discount in hashmap
        ProductWithDetails productDetails = optProdcut.get();

        //set asset url for List of products asset and thumnailurl
        List<ProductAsset> productAssets = optProdcut.get().getProductAssets();
        for(ProductAsset pa : productAssets){
            pa.setUrl(assetServiceUrl+pa.getUrl());
            //handle null
                if(pa.getUrl() == null){
                pa.setUrl(null);
            }
            
        }
        productDetails.setProductAssets(productAssets);
        productDetails.setThumbnailUrl(assetServiceUrl+optProdcut.get().getThumbnailUrl());
        //handle null
        if(optProdcut.get().getThumbnailUrl() == null){
            productDetails.setThumbnailUrl(null);

        }
        
        for (int i=0;i<productDetails.getProductInventories().size();i++) {
            ProductInventoryWithDetails productInventory = productDetails.getProductInventories().get(i);
            //ItemDiscount discountDetails = discountedItemMap.get(productInventory.getItemCode());
            ItemDiscount discountDetails = ProductDiscount.getItemDiscount(storeDiscountRepository, storeId, productInventory.getItemCode(), regionCountry);
            if (discountDetails!=null) {
                double discountedPrice = productInventory.getPrice();
                if (discountDetails.calculationType.equals(DiscountCalculationType.FIX)) {
                    discountedPrice = productInventory.getPrice() - discountDetails.discountAmount;
                } else if (discountDetails.calculationType.equals(DiscountCalculationType.PERCENT)) {
                    discountedPrice = productInventory.getPrice() - (discountDetails.discountAmount / 100 * productInventory.getPrice());
                }
                discountDetails.discountedPrice = discountedPrice;
                discountDetails.normalPrice = productInventory.getPrice();
                productInventory.setItemDiscount(discountDetails); 
            } else {
                //get inactive discount if any
                List<StoreDiscountProduct> discountList = storeDiscountProductRepository.findByItemCode(productInventory.getItemCode());
                if (!discountList.isEmpty()) {
                    StoreDiscountProduct storeDiscountProduct = discountList.get(0);
                    ItemDiscount inactiveDiscount = new ItemDiscount();
                    inactiveDiscount.discountId = storeDiscountProduct.getStoreDiscountId();
                    productInventory.setItemDiscountInactive(inactiveDiscount);                    
                }
            }
        }
        response.setStatus(HttpStatus.OK);
        response.setData(productDetails);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-products-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-products-delete-by-id', 'all')  and @customOwnerVerifier.VerifyStore(#storeId)")
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
    @PreAuthorize("hasAnyAuthority('store-products-put-by-id', 'all')  and @customOwnerVerifier.VerifyStore(#storeId)")
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
    @PreAuthorize("hasAnyAuthority('store-products-post', 'all')  and @customOwnerVerifier.VerifyStore(#storeId)")
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

        //String seoName = generateSeoName(bodyProduct.getName());
        
        String seoName = bodyProduct.getSeoName();
        
        String seoUrl = productSeoUrl.replace("{{store-domain}}", optStore.get().getDomain());
        seoUrl = seoUrl.replace("{{product-name}}", seoName);
        bodyProduct.setSeoUrl(seoUrl);

        bodyProduct.setSeoName(seoName);
        if (bodyProduct.getIsPackage()==null) { bodyProduct.setIsPackage(Boolean.FALSE); }

        //set image url 


        Product savedProduct = productRepository.save(bodyProduct);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product added to store with storeId: {}, productId: {}" + storeId, savedProduct.getId());

        response.setStatus(HttpStatus.CREATED);
        response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping(path = {"/checkname"}, name = "store-products-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-products-get', 'all')")
    public ResponseEntity<HttpResponse> checkNameAvailability(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(required = true) String productName
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "checkname productName: " + productName, "");

        List<Product> productList = productRepository.findByNameAndStoreIdAndStatusNot(productName, storeId, "DELETED");

        if (productList.isEmpty()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Name: " + productName+" IS available");
            response.setStatus(HttpStatus.OK);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Name: " + productName+" NOT available");
            response.setStatus(HttpStatus.CONFLICT);
            return ResponseEntity.status(response.getStatus()).body(response);
        }               
        
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
    
    
    public Specification<ProductWithDetails> getStoreProductSpec(
            String name, String seoName, String categoryId, 
            List<String> statusList, Example<ProductWithDetails> example) {

        return (Specification<ProductWithDetails>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(builder.equal(root.get("name"), name));
            }
            if (seoName != null) {
                predicates.add(builder.equal(root.get("seoName"), seoName));
            }
            if (categoryId != null) {
                predicates.add(builder.equal(root.get("categoryId"), categoryId));
            }
            if (statusList!=null) {
                int statusCount = statusList.size();
                List<Predicate> statusPredicatesList = new ArrayList<>();
                for (int i=0;i<statusList.size();i++) {
                    Predicate predicateForCompletionStatus = builder.equal(root.get("status"), statusList.get(i));
                    statusPredicatesList.add(predicateForCompletionStatus);
                }
                Predicate finalPredicate = builder.or(statusPredicatesList.toArray(new Predicate[statusCount]));
                predicates.add(finalPredicate);
            }
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}
