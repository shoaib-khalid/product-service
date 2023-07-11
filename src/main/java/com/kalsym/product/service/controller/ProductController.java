package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.DiscountCalculationType;
import com.kalsym.product.service.model.ItemDiscount;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.model.product.ProductAsset;
import com.kalsym.product.service.model.product.ProductInventoryWithDetails;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.StoreDiscountProduct;
import com.kalsym.product.service.model.store.StoreWithDetails;
import com.kalsym.product.service.model.store.object.CustomPageable;
import com.kalsym.product.service.repository.*;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductWithDetails;
import com.kalsym.product.service.utility.Logger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;

import com.kalsym.product.service.utility.ProductDiscount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.utility.Validation;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/products")
public class ProductController {

    @Autowired()
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
    StoreDiscountProductRepository storeDiscountProductRepository;

    @Autowired
    StoreDiscountRepository storeDiscountRepository;

    @Autowired
    RegionCountriesRepository regionCountriesRepository;

    @Value("${asset.service.url}")
    private String assetServiceUrl;


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
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false, defaultValue = "name") String sortByCol,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        if (status == null) {
            status = new ArrayList();
            status.add("ACTIVE");
            status.add("INACTIVE");
            status.add("OUTOFSTOCK");
        }

        ProductWithDetails productMatch = new ProductWithDetails();
        
        Pageable pageable = PageRequest.of(page, pageSize);
        productMatch.setStoreId(storeId);
        productMatch.setCategoryId(categoryId);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductWithDetails> example = Example.of(productMatch, matcher);

        Page<ProductWithDetails> fetchedPage = productWithDetailsRepository.findAll(getProductSpec(categoryId, storeId, status, name, sortByCol,sortingOrder, example), pageable);
        List<ProductWithDetails> productList = fetchedPage.getContent();

        ProductWithDetails[] productWithDetailsList = new ProductWithDetails[productList.size()];
        for (int x=0;x<productList.size();x++) {
            //check for item discount in hashmap
            ProductWithDetails productDetails = productList.get(x);

            if (storeId == null) {
                storeId = productDetails.getStoreId();
            }

            Optional<Store> optStore = storeRepository.findById(storeId);

            if (!optStore.isPresent()) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
                response.setStatus(HttpStatus.NOT_FOUND);
                response.setError("store not found");
                return ResponseEntity.status(response.getStatus()).body(response);
            }

            //get reqion country for store
            RegionCountry regionCountry = null;
            Optional<RegionCountry> optRegion = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
            if (optRegion.isPresent()) {
                regionCountry = optRegion.get();
            }

            //set asset url for List of products asset and thumnailurl
            List<ProductAsset> productAssets = productDetails.getProductAssets();
            for(ProductAsset pa : productAssets){
                //handle null
                if(pa.getUrl() != null){
                    pa.setUrl(assetServiceUrl+pa.getUrl());

                } else{
                    pa.setUrl(null);

                }
            }
            productDetails.setProductAssets(productAssets);
            //handle null
            if(productDetails.getThumbnailUrl() != null){
                productDetails.setImageUrl(productDetails.getThumbnailUrl());
                productDetails.setThumbnailUrl(assetServiceUrl+productDetails.getThumbnailUrl());

            } else{
                productDetails.setThumbnailUrl(null);
                productDetails.setImageUrl(null);
            }

            for (int i=0;i<productDetails.getProductInventories().size();i++) {
                ProductInventoryWithDetails productInventory = productDetails.getProductInventories().get(i);
                //ItemDiscount discountDetails = discountedItemMap.get(productInventory.getItemCode());
                /*ItemDiscount discountDetails = hashmapLoader.GetDiscountedItemMap(storeId, productInventory.getItemCode());*/
                ItemDiscount discountDetails = ProductDiscount.getItemDiscount(storeDiscountRepository, productDetails.getStoreId(), productInventory.getItemCode(), regionCountry);
                if (discountDetails != null) {
                    double discountedPrice = productInventory.getPrice();
                    double dineInDiscountedPrice = productInventory.getDineInPrice();
                    if (discountDetails.calculationType.equals(DiscountCalculationType.FIX)) {
                        discountedPrice = productInventory.getPrice() - discountDetails.discountAmount;
                    } else if (discountDetails.calculationType.equals(DiscountCalculationType.PERCENT)) {
                        discountedPrice = productInventory.getPrice() - (discountDetails.discountAmount / 100 * productInventory.getPrice());
                    }


                    if(discountDetails.dineInCalculationType!=null && discountDetails.dineInCalculationType.equals(DiscountCalculationType.FIX)){
                        dineInDiscountedPrice = productInventory.getDineInPrice() - discountDetails.dineInDiscountAmount;

                    }
                    else if (discountDetails.dineInCalculationType!=null && discountDetails.dineInCalculationType.equals(DiscountCalculationType.PERCENT)) {
                        dineInDiscountedPrice = productInventory.getDineInPrice() - (discountDetails.dineInDiscountAmount / 100 * productInventory.getDineInPrice());
                    }

                    discountDetails.discountedPrice = discountedPrice;
                    discountDetails.normalPrice = productInventory.getPrice();

                    discountDetails.dineInDiscountedPrice= dineInDiscountedPrice;
                    discountDetails.dineInNormalPrice = productInventory.getDineInPrice();

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

            //sort the product inventories by price acsending
            List<ProductInventoryWithDetails> sortProductInventories = productDetails.getProductInventories().stream()
                    .sorted(Comparator.comparingDouble(ProductInventoryWithDetails::getPrice))
                    .collect(Collectors.toList());

            //set the product inventories data for sort price ascending
            productDetails.setProductInventories(sortProductInventories);

            productWithDetailsList[x]=productDetails;
        }

        //create custom pageable object with modified content
        CustomPageable customPageable = new CustomPageable();
        customPageable.content = productWithDetailsList;
        customPageable.pageable = fetchedPage.getPageable();
        customPageable.totalPages = fetchedPage.getTotalPages();
        customPageable.totalElements = fetchedPage.getTotalElements();
        customPageable.last = fetchedPage.isLast();
        customPageable.size = fetchedPage.getSize();
        customPageable.number = fetchedPage.getNumber();
        customPageable.sort = fetchedPage.getSort();
        customPageable.numberOfElements = fetchedPage.getNumberOfElements();
        customPageable.first  = fetchedPage.isFirst();
        customPageable.empty = fetchedPage.isEmpty();

        response.setStatus(HttpStatus.OK);
        response.setData(customPageable);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public Specification<ProductWithDetails> getProductSpec(
            String categoryId, String storeId,
            List<String> statusList, String name, String sortByCol, Sort.Direction sortingOrder, Example<ProductWithDetails> example) {

        return (Specification<ProductWithDetails>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<ProductWithDetails, ProductInventoryWithDetails> productInventories = root.join("productInventories", JoinType.INNER);

            if (categoryId != null) {
                predicates.add(builder.equal(root.get("categoryId"), categoryId));
            }
            if (storeId != null ){
                predicates.add(builder.equal(root.get("storeId"), storeId));
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

            if (name != null) {
                predicates.add(builder.like(root.get("name"), "%"+name+"%"));
            }

            List<Order> orderList = new ArrayList<Order>();

            if (sortingOrder==Sort.Direction.ASC){
                if(sortByCol.equals("price")){

                    orderList.add(builder.asc(productInventories.get(sortByCol)));

                } else if(sortByCol.equals("dineInPrice")){
                    orderList.add(builder.asc(productInventories.get(sortByCol)));

                }
                else{
                    orderList.add(builder.asc(root.get(sortByCol)));

                }

            }else{

                if(sortByCol.equals("price")){

                    orderList.add(builder.desc(productInventories.get(sortByCol)));


                }else if(sortByCol.equals("dineInPrice")){
                    orderList.add(builder.desc(productInventories.get(sortByCol)));

                }
                else{
                    orderList.add(builder.desc(root.get(sortByCol)));

                }


            }
            query.orderBy(orderList);
            query.distinct(true);
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    @GetMapping(path = {"/{id}"}, name = "products-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('products-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getProduct(HttpServletRequest request,
            @PathVariable(required = false) String id,
            @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        Optional<ProductWithDetails> optProduct = productWithDetailsRepository.findById(id);

        if (!optProduct.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(optProduct.get());
        return ResponseEntity.status(response.getStatus()).body(response);
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
            // @RequestParam(required = false, defaultValue = "true") boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");        
        
        Product productMatch = new Product();

        System.out.println("ggggggggggggggggggggggggggggggggggggggggggggggggggg============================="+storeId);

        Pageable pageable = PageRequest.of(page, pageSize);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Product> example = Example.of(productMatch, matcher);

        Specification<Product> productSpecs = searchProductSpecs(storeId, name,example);

        Page<Product> data = productRepository.findAll(productSpecs, pageable);

        // TO SET THUMBNAIL URL
        for (Product p : data.getContent()){

            p.setThumbnailUrl(assetServiceUrl+p.getThumbnailUrl());
        }

        response.setStatus(HttpStatus.OK);
        response.setData(data);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    //@PreAuthorize("hasAnyAuthority('products-delete-by-id', 'all') and projectAccessHandler.validatedProductOwner(#id)")
    
    @DeleteMapping(path = {"/{id}"}, name = "products-delete-by-id")
    @PreAuthorize("hasAnyAuthority('products-delete-by-id','all') and @customOwnerVerifier.VerifyProduct(#id)")
    public ResponseEntity<HttpResponse> deleteProductById(HttpServletRequest request,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        Optional<Product> optProduct = productRepository.findById(id);

        if (!optProduct.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND: " + id, "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        if (!Validation.VerifyStoreId(optProduct.get().getStoreId(), storeRepository)) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Unathorized productId", "");
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setError("Unathorized productId");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product found", "");
        productRepository.delete(optProduct.get());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product deleted, with id: {}", id);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     *
     * @param request
     * @param id
     * @param bodyProduct
     * @return
     */
    @PutMapping(path = {"/{id}"}, name = "products-put-by-id")
    @PreAuthorize("hasAnyAuthority('products-put-by-id', 'all') and @customOwnerVerifier.VerifyProduct(#id)")
    public ResponseEntity<HttpResponse> putProductById(HttpServletRequest request,
            @PathVariable String id,
            @RequestBody Product bodyProduct) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        Optional<Product> optProduct = productRepository.findById(id);

        if (!optProduct.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "NOT_FOUND: {}", id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        if (!Validation.VerifyStoreId(optProduct.get().getStoreId(), storeRepository)) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Unathorized productId", "");
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setError("Unathorized productId");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product found with productId: {}", id);
        Product product = optProduct.get();
        List<String> errors = new ArrayList<>();

        product.update(bodyProduct);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product updated for productId: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(productRepository.save(product));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    public static Specification<Product> searchProductSpecs(
        String storeId, 
        String name, 
        Example<Product> example) {

        return (Specification<Product>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (storeId != null && !storeId.isEmpty()) {
                predicates.add(builder.equal(root.get("storeId"), storeId));
            }

            if (name != null && !name.isEmpty()) {
                predicates.add(builder.like(root.get("name"), "%"+name+"%"));
            }

            predicates.add(root.get("thumbnailUrl").isNotNull());
            
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }




}
