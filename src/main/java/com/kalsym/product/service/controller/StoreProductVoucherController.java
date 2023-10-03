package com.kalsym.product.service.controller;

//Importing Models

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.*;
import com.kalsym.product.service.model.ItemDiscount;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.model.product.*;
import com.kalsym.product.service.model.request.ProductVoucherRequest;
import com.kalsym.product.service.model.request.ProductVoucherTermsRequest;
import com.kalsym.product.service.model.store.*;
import com.kalsym.product.service.model.store.object.CustomPageable;
import com.kalsym.product.service.repository.*;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;
import com.kalsym.product.service.utility.ProductDiscount;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author ayaan
 */
@RestController
@RequestMapping("/product/voucher")
public class StoreProductVoucherController {

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherServiceTypeRepository voucherServiceTypeRepository;

    @Autowired
    VoucherStoreRepository voucherStoreRepository;

    @Autowired
    VoucherTermsRepository voucherTermsRepository;

    @Autowired
    VoucherVerticalRepository voucherVerticalRepository;

    @Autowired
    VoucherSerialNumberRepository voucherSerialNumberRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreDiscountRepository storeDiscountRepository;

    @Autowired
    StoreDiscountProductRepository storeDiscountProductRepository;

    @Autowired
    ProductInventoryRepository productInventoryRepository;

    @Autowired
    StoreProductController storeProductController;

    @Autowired
    StoreProductInventoryController storeProductInventoryController;

    @Autowired
    ProductWithDetailsRepository productWithDetailsRepository;

    @Autowired
    RegionCountriesRepository regionCountriesRepository;

    @Value("${asset.service.url}")
    String assetServiceUrl;

    @GetMapping(path = {"/available"})
    public ResponseEntity<HttpResponse> getAvailableVoucher(
            HttpServletRequest request,
            @RequestParam(required = false) VoucherType voucherType,
            @RequestParam(required = false) String verticalCode,
            @RequestParam(required = false) String voucherCode,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String voucherStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                logprefix, "voucherType:" + voucherType + " storeId:" + storeId);

        Voucher voucherMatch = new Voucher();
        voucherMatch.setStatus(VoucherStatus.ACTIVE);

        // Use the parseVoucherStatus method to handle voucherStatus safely
        VoucherStatus parsedVoucherStatus = parseVoucherStatus(voucherStatus);
        if (parsedVoucherStatus != null) {
            voucherMatch.setStatus(parsedVoucherStatus);
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Voucher> example = Example.of(voucherMatch, matcher);

        Specification<Voucher> voucherSpec = VoucherSearchSpecs.getSpecWithDatesBetween(new Date(), voucherType, storeId, verticalCode, voucherCode, parsedVoucherStatus, example);
        Page<Voucher> voucherWithPage = voucherRepository.findAll(voucherSpec, pageable);

        response.setStatus(HttpStatus.OK);
        response.setData(voucherWithPage);

        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @GetMapping(path = {"/all-vouchers"})
    public ResponseEntity<HttpResponse> getAllVouchers(HttpServletRequest request,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String voucherCode,
                                                       @RequestParam String storeId,
                                                       @RequestParam(required = false) List<String> voucherStatus,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int pageSize,
                                                       @RequestParam(required = false, defaultValue = "name") String sortByCol,
                                                       @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder
    ) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " storeId:" + storeId);

//        Voucher voucherMatch = new Voucher();
//
//        Pageable pageable = PageRequest.of(page, pageSize);
//        ExampleMatcher matcher = ExampleMatcher
//                .matchingAll()
//                .withIgnoreCase()
//                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
//        Example<Voucher> example = Example.of(voucherMatch, matcher);

//        Specification<Voucher> voucherSpec = VoucherSearchSpecs.getSpecWithDatesBetween(null, voucherType, storeId, verticalCode, voucherCode, parseVoucherStatus(voucherStatus), example);
//        Page<Voucher> voucherWithPage = voucherRepository.findAll(voucherSpec, pageable);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        // Get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(optStore.get().getRegionCountryId());
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        ProductWithDetails productMatch = new ProductWithDetails();
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductWithDetails> productExample = Example.of(productMatch, matcher);

        Specification<ProductWithDetails> productWithDetailsSpec = VoucherSearchSpecs.getProductVoucherSpec(name, storeId, voucherStatus, productExample, sortByCol, sortingOrder);
        Page<ProductWithDetails> productWithPage = productWithDetailsRepository.findAll(productWithDetailsSpec, pageable);
        List<ProductWithDetails> productList = productWithPage.getContent();

        ProductWithDetails[] productWithDetailsList = new ProductWithDetails[productList.size()];
        for (int x=0;x<productList.size();x++) {
            //check for item discount in hashmap
            ProductWithDetails productDetails = productList.get(x);

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
                ItemDiscount discountDetails = ProductDiscount.getItemDiscount(storeDiscountRepository, storeId, productInventory.getItemCode(), regionCountry);
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

        response.setStatus(HttpStatus.OK);
        response.setData(customPageable);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private VoucherStatus parseVoucherStatus(String voucherStatus) {
        if (voucherStatus != null) {
            try {
                return VoucherStatus.valueOf(voucherStatus);
            } catch (IllegalArgumentException e) {
                // To Do
                // Handle the case when the input voucherStatus does not match any enum constant
            }
        }
        return null; // Return null if voucherStatus is null or invalid
    }

    @GetMapping(path = {"/all-vouchers/{id}"})
    public ResponseEntity<HttpResponse> getAllVouchersById(HttpServletRequest request,
                                                           @PathVariable String id) {

        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "id:" + id);

        Optional<Voucher> voucherOptional = voucherRepository.findById(id);

        if (!voucherOptional.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "voucher NOT_FOUND: " + id);

            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("Voucher not found");
            return ResponseEntity.status(response.getStatus()).body(response);

        }

        Optional<ProductWithDetails> productWithDetailsOptional = productWithDetailsRepository.findByVoucherId(voucherOptional.get().getId());

        if (!productWithDetailsOptional.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "product voucher NOT_FOUND: " + voucherOptional.get().getId());
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("Product for this voucher not found");
            return ResponseEntity.status(response.getStatus()).body(response);

        }
        response.setStatus(HttpStatus.OK);
        response.setData(productWithDetailsOptional.get());

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/check-voucher-code"})
    public ResponseEntity<HttpResponse> checkVoucherCode(HttpServletRequest request,
                                                         @RequestParam() String voucherRedeemCode
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Voucher Redeem Code:" + voucherRedeemCode);

        VoucherSerialNumber voucherSerialNumber = voucherSerialNumberRepository.findByVoucherRedeemCode(voucherRedeemCode);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                logprefix, "Voucher Serial Number:" + voucherSerialNumber);

        if (voucherSerialNumber == null) {
            response.setMessage("Invalid voucher code.");
            response.setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        if (!voucherSerialNumber.getCurrentStatus().equals(VoucherCurrentStatus.NEW)) {
            response.setMessage("Voucher is not in NEW status.");
            response.setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        if ( voucherSerialNumber.getExpiryDate().before(new Date())) {
            response.setMessage("Voucher is expired and cannot be redeemed.");
            response.setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        String voucherId = voucherSerialNumber.getVoucherId();
        Optional<Voucher> voucherOptional = voucherRepository.findById(voucherId);

        if (!voucherOptional.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND voucher with ID: " + voucherId);
            response.setError("Voucher not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Voucher voucher = voucherOptional.get();

        if (voucher.getTotalRedeem() > 0) {
            response.setMessage("Voucher is not valid for redemption.");
            response.setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        // For successful validation
        response.setStatus(HttpStatus.OK);
        response.setMessage("Voucher code is valid and can be redeemed.");

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/get-voucher-status"})
    public ResponseEntity<HttpResponse> getVoucherStatus(HttpServletRequest request,
                                                         @RequestParam() String voucherRedeemCode
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                logprefix, "Voucher Redeem Code:" + voucherRedeemCode);

        VoucherSerialNumber voucherSerialNumber = voucherSerialNumberRepository.
                findByVoucherRedeemCode(voucherRedeemCode);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                logprefix, "Voucher Serial Number:" + voucherSerialNumber);

        if (voucherSerialNumber == null) {
            response.setMessage("Invalid voucher code.");
            response.setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        // For successful validation
        response.setStatus(HttpStatus.OK);
        response.setData(voucherSerialNumber.getCurrentStatus());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // TODO:
    //  Add the logic to check if the voucher is valid for redemption
    @PutMapping(path = {"/redeem"}, name = "voucher-redeem")
    public ResponseEntity<HttpResponse> redeemVoucher(HttpServletRequest request,
                                                      @RequestParam() String voucherRedeemCode,
                                                      @RequestParam() String phoneNumber,
                                                      @RequestParam() String storeId
    )
    {
        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                logprefix, "Voucher Redeem Code:" + voucherRedeemCode);

        VoucherSerialNumber voucherSerialNumber = voucherSerialNumberRepository.
                findByVoucherRedeemCode(voucherRedeemCode);

        // Check if the voucherRedeemCode is valid
        if (voucherSerialNumber == null) {
            response.setMessage("Invalid voucher code.");
            response.setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else{
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                    logprefix, "Voucher Serial Number:" + voucherSerialNumber);
        }

        // Check if the voucher is not in NEW status
        if (voucherSerialNumber.getCurrentStatus().equals(VoucherCurrentStatus.NEW)){
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                    logprefix, "Voucher is in New Status");
            response.setMessage("Voucher is in NEW status.");
            response.setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        if (voucherSerialNumber.getCurrentStatus().equals(VoucherCurrentStatus.USED)) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                    logprefix, "Voucher is Used already");
            response.setMessage("The voucher have already been used, try different one.");
            response.setStatus(HttpStatus.CONFLICT);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        // Check if the voucher has not expired
        if (new Date().after(voucherSerialNumber.getExpiryDate())) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                    logprefix, "Voucher is in expired");
            response.setMessage("The voucher have expired, try different one.");
            response.setStatus(HttpStatus.CONFLICT);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        String voucherId = voucherSerialNumber.getVoucherId();
        Optional<Voucher> voucherOptional = voucherRepository.findById(voucherId);

        if (!voucherOptional.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                    logprefix, " NOT_FOUND voucher with ID: " + voucherId);
            response.setError("Voucher not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        // Check if the voucher is from same store as store id passed
        Voucher voucher = voucherOptional.get();
        if(!voucher.getStoreId().equals(storeId)){
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                    logprefix, "Voucher is not of this store");
            response.setMessage("The voucher is not from the same store, try different one.");
            response.setStatus(HttpStatus.CONFLICT);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

//        Voucher voucher = voucherOptional.get();
//        voucher.setTotalRedeem(voucher.getTotalRedeem()+1);
//        voucher.setTotalQuantity(voucher.getTotalQuantity()-1);

        if (phoneNumber.equals(voucherSerialNumber.getCustomer())
                && voucherSerialNumber.getCurrentStatus().equals(VoucherCurrentStatus.BOUGHT)) {
            // Update the voucherRedeemCode
            voucherSerialNumber.setCurrentStatus(VoucherCurrentStatus.USED);
            voucherSerialNumber.setIsUsed(true);
            voucherSerialNumber.update(voucherSerialNumber);
            voucherSerialNumberRepository.save(voucherSerialNumber);

        } else {
            response.setMessage("The voucher phone number is wrong, try a different one.");
            response.setStatus(HttpStatus.CONFLICT);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

//
//        voucher.update(voucher);
//        voucherRepository.save(voucher);

        response.setStatus(HttpStatus.OK);
        response.setMessage("Voucher was redeemed Successfully");
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ApiOperation(value = "Create voucher", notes = "Note: Include storeId for STORE voucher type.")
    @PostMapping(path = {"/create"}, name = "product-voucher-post")
    @PreAuthorize("hasAnyAuthority('product-voucher-post', 'all')")
    public ResponseEntity<HttpResponse> postVoucher(HttpServletRequest request,
                                                    @RequestParam() String storeId,
                                                    @RequestParam() String categoryId,
                                                    @Valid @RequestBody ProductVoucherRequest voucherBody) {
        String logprefix = "postVoucher()";
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "bodyProduct: " + voucherBody.toString());

//        Auth auth = null;
//        ObjectMapper mapper = new ObjectMapper();
//        auth = mapper.convertValue(authResponse.getBody().getData(), Auth.class);
//        System.out.println("USER_ROLE=" + );

        Optional<Store> optionalStore = storeRepository.findById(storeId);

        if (!optionalStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setError("Store not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Optional<StoreCategory> optionalStoreCategory = storeCategoryRepository.findById(categoryId);

        if (!optionalStoreCategory.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND categoryId: " + categoryId);
            response.setError("Category not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        // Save to voucher table
        Voucher voucherToSave = new Voucher();

        voucherToSave.setVoucherType(VoucherType.STORE);
        voucherToSave.setStoreId(storeId);
        voucherToSave.setName(voucherBody.getName());
        voucherToSave.setDiscountValue(voucherBody.getVoucherValue());
        voucherToSave.setMaxDiscountAmount(voucherBody.getVoucherValue());
        voucherToSave.setVoucherCode(voucherBody.getVoucherCode());
        voucherToSave.setTotalQuantity(voucherBody.getTotalQuantity());
        voucherToSave.setTotalRedeem(0);
        voucherToSave.setCurrencyLabel(voucherBody.getCurrencyLabel());
        voucherToSave.setIsNewUserVoucher(true);
        voucherToSave.setCheckTotalRedeem(true);
        voucherToSave.setMinimumSpend(1.0);
        voucherToSave.setAllowDoubleDiscount(true);
        voucherToSave.setRequireToClaim(false);
        voucherToSave.setStatus(VoucherStatus.ACTIVE);
        voucherToSave.setVoucherType(VoucherType.STORE);
        voucherToSave.setDiscountType(VoucherDiscountType.TOTALSALES);
        voucherToSave.setGroupType(VoucherGroupType.CASH);
        voucherToSave.setCalculationType(DiscountCalculationType.FIX);
        voucherToSave.setStartDate(voucherBody.getStartDate());
        voucherToSave.setEndDate(voucherBody.getEndDate());
        voucherToSave.setCreated_at(new Date());
        voucherToSave.setUpdated_at(new Date());

        Voucher savedVoucher = voucherRepository.save(voucherToSave);

        // Save to voucher_store table
        VoucherStore voucherStoreToSave = new VoucherStore();

        voucherStoreToSave.setVoucherId((savedVoucher.getId()));
        voucherStoreToSave.setStoreId(storeId);
        voucherStoreRepository.save(voucherStoreToSave);


        // Save to voucher_service_type table
        // Create and save the DINEIN VoucherServiceType
        VoucherServiceType dineInServiceType = new VoucherServiceType();
        dineInServiceType.setVoucherId(savedVoucher.getId());
        dineInServiceType.setServiceType("DINEIN");
        voucherServiceTypeRepository.save(dineInServiceType);

        // Create and save the DELIVERIN VoucherServiceType
        VoucherServiceType deliverInServiceType = new VoucherServiceType();
        deliverInServiceType.setVoucherId(savedVoucher.getId());
        deliverInServiceType.setServiceType("DELIVERIN");
        voucherServiceTypeRepository.save(deliverInServiceType);


        // Save to voucher_terms table
        if (!voucherBody.getVoucherTerms().isEmpty()) {
            for (ProductVoucherTermsRequest voucherTermsRequest: voucherBody.getVoucherTerms()) {
                VoucherTerms voucherTerms = new VoucherTerms();

                voucherTerms.setVoucherId(savedVoucher.getId());
                voucherTerms.setTerms(voucherTermsRequest.getTerms());
                voucherTermsRepository.save(voucherTerms);
            }
        }

        // Save to voucher_vertical table
        VoucherVertical voucherVerticalToSave = new VoucherVertical();

        voucherVerticalToSave.setVoucherId(savedVoucher.getId());
        voucherVerticalToSave.setVerticalCode(voucherBody.getVerticalCode());
        voucherVerticalRepository.save(voucherVerticalToSave);

        // Delete data from DB
        voucherSerialNumberRepository.deleteByVoucherId(savedVoucher.getId());
        // Save to voucher_vertical table
        int totalQuantity = voucherBody.getTotalQuantity();

        for (int i = 0; i < totalQuantity; i++) {
            VoucherSerialNumber voucherSerialNumber = new VoucherSerialNumber(); // Create a new instance inside the loop

            voucherSerialNumber.setVoucherId(savedVoucher.getId());
            voucherSerialNumber.setExpiryDate(savedVoucher.getEndDate());
            voucherSerialNumber.setIsUsed(false);
            voucherSerialNumber.setCurrentStatus(VoucherCurrentStatus.NEW);

            VoucherSerialNumber savedVoucherSerialNumber =  voucherSerialNumberRepository.save(voucherSerialNumber);

            voucherSerialNumber.setVoucherRedeemCode(VoucherSerialNumber.generateUniqueRedeemCode(voucherBody.getName(), voucherSerialNumber.getId()));
            String voucherRedeemCode = savedVoucherSerialNumber.getVoucherRedeemCode();
            savedVoucherSerialNumber.setSerialNumber(VoucherSerialNumber.generateUniqueSerialNumber(voucherRedeemCode));

            voucherSerialNumber.update(savedVoucherSerialNumber);
            voucherSerialNumberRepository.save(voucherSerialNumber);
        }

        // Create a new Product and Product Inventory entity based on the voucher information
        Product productForVoucher = new Product();
        ProductInventory productForInventory  = new ProductInventory();

        // Product table update--------------------------
        // Set the product name based on the voucher name
        productForVoucher.setName(voucherBody.getName());

        List<Product> products = productRepository.findByStoreId(storeId);
        List<String> errors = new ArrayList<>();

        for (Product existingProduct : products) {
            if (existingProduct.getName().equals(productForVoucher.getName()) && !"DELETED".equalsIgnoreCase(existingProduct.getStatus())) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "productName already exists", "");
                // DELETE voucher
                voucherRepository.deleteById(savedVoucher.getId());
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("Product name already exists");
                response.setData(errors);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }

        String SkuGenerated = Product.generateSku(voucherBody.getName());

        productForVoucher.setDescription(voucherBody.getDescription());
        // Set the voucher id in the product table
        productForVoucher.setVoucherId(savedVoucher.getId());
        // Set the category id in the product table
        productForVoucher.setCategoryId(categoryId);
        // Set the voucher's storeId in Product table
        productForVoucher.setStoreId(storeId);
        // Set the specific value for 'allowOutOfStockPurchases' to false
        productForVoucher.setAllowOutOfStockPurchases(false);
        // Set the custom price as false
        productForVoucher.setCustomPrice(false);
        // Set track quantity as true
        productForVoucher.setTrackQuantity(true);
        // set allow out of stock option as false
        productForVoucher.setAllowOutOfStockPurchases(false);
        // set minimum quantity for alarm to -1
        productForVoucher.setMinQuantityForAlarm(-1);
        productForVoucher.setPackingSize(null);
        productForVoucher.setVehicleType(VehicleType.MOTORCYCLE);
        productForVoucher.setStatus(voucherBody.getStatus().toString());
        productForVoucher.setIsPackage(false);
        productForVoucher.setIsNoteOptional(true);
        productForVoucher.setCustomNote("");
        productForVoucher.setHasAddOn(false);
        productForVoucher.setSeoName(SkuGenerated);
        productForVoucher.setSeoUrl("");
        // Set product type as DIGITAL
        productForVoucher.setProductType(ProductType.DIGITAL);

        Product createdProduct = new Product();

        try {
            // Make the web service call and get the ResponseEntity
            ResponseEntity<HttpResponse> responseEntity = storeProductController.postStoreProduct(request, storeId, productForVoucher);

            // Check the HTTP status code of the response
            HttpStatus statusCode = responseEntity.getStatusCode();

            if (statusCode.is2xxSuccessful()) {
                // Successful response, get the created product from the response body
                createdProduct = (Product) responseEntity.getBody().getData();
                Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product created: " + createdProduct);

                // Further processing with the savedProduct
            } else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                // Handle client or server errors, e.g., validation errors or internal server errors

                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Error while creating Product", "");
                // DELETE voucher
                voucherRepository.deleteById(savedVoucher.getId());
                response.setStatus(HttpStatus.BAD_REQUEST);
                errors.add("Error while creating Product");
                response.setData(errors);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception e) {
            // Handle general exceptions that might occur during the web service call
            // For example, network issues, timeouts, or any unexpected exceptions

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Exception_prod==" + e.getMessage());
            // DELETE voucher
            voucherRepository.deleteById(savedVoucher.getId());
            response.setStatus(HttpStatus.BAD_REQUEST);
            errors.add("Error while creating Product");
            response.setData(errors);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        ProductInventory createdProductInventory = new ProductInventory();
        productForInventory.setProductId(createdProduct.getId());
        // Product inventory table update--------------------------
        // set product id
        productForInventory.setProductId(createdProduct.getId());
        productForInventory.setItemCode(createdProduct.getId()+ "aa");
        // set quantity from voucher body
        productForInventory.setQuantity(voucherBody.getTotalQuantity());
        productForInventory.setPrice(voucherBody.getSellingPrice());
        productForInventory.setDineInPrice(voucherBody.getSellingPrice());
        productForInventory.setCostPrice(voucherBody.getSellingPrice());
        productForInventory.setCompareAtprice(0.0);
        productForInventory.setSKU(SkuGenerated);
        productForInventory.setStatus("AVAILABLE");

        try {
            // Make the web service call and get the ResponseEntity
            ResponseEntity<HttpResponse> responseEntity = storeProductInventoryController.postStoreProductInventorys(request, storeId, productForInventory.getProductId(), productForInventory);

            // Check the HTTP status code of the response
            HttpStatus statusCode = responseEntity.getStatusCode();

            if (statusCode.is2xxSuccessful()) {
                // Successful response, get the created product inventory from the response body
                createdProductInventory = (ProductInventory) responseEntity.getBody().getData();
                Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product inventory created: " + createdProductInventory);

            } else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                // Handle client or server errors, e.g., validation errors or internal server errors

                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Error while creating Product Inventory", "");
                // DELETE voucher
                voucherRepository.deleteById(savedVoucher.getId());
                response.setStatus(HttpStatus.BAD_REQUEST);
                errors.add("Error while creating Product Inventory");
                response.setData(errors);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception e) {
            // Handle general exceptions that might occur during the web service call
            // For example, network issues, timeouts, or any unexpected exceptions

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Exception_prodInv==" + e.getMessage());
            // DELETE voucher
            voucherRepository.deleteById(savedVoucher.getId());
            response.setStatus(HttpStatus.BAD_REQUEST);
            errors.add("Error while creating Product Inventory");
            response.setData(errors);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        // Refresh repositories to get the latest data
        voucherRepository.refresh(savedVoucher);
        productRepository.refresh(createdProduct);
        Optional<Product> optionalProduct = productRepository.findById(createdProduct.getId());

        response.setStatus(HttpStatus.CREATED);
        response.setData(optionalProduct.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PutMapping(path = {"/edit/{id}"}, name = "product-voucher-put")
    @PreAuthorize("hasAnyAuthority('product-voucher-put', 'all')")
    public ResponseEntity<HttpResponse> putVoucher(HttpServletRequest request,
                                                   @PathVariable String id,
                                                   @RequestBody ProductVoucherRequest voucherBody) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "id: " + id);

        Optional<Voucher> voucherOptional = voucherRepository.findById(id);

        if (!voucherOptional.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND voucher with ID: " + id);
            response.setError("Voucher not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Voucher voucher = voucherOptional.get();
        String oldName = voucher.getName();
        int oldVoucherQuantity = voucher.getTotalQuantity();
        // Update voucher details based on the request body
        // Save to voucher table
        voucher.setName(voucherBody.getName());
        voucher.setDiscountValue(voucherBody.getVoucherValue());
        voucher.setMaxDiscountAmount(voucherBody.getVoucherValue());
        voucher.setVoucherCode(voucherBody.getVoucherCode());
        voucher.setTotalQuantity(voucherBody.getTotalQuantity());
        voucher.setCurrencyLabel(voucherBody.getCurrencyLabel());
        voucher.setStartDate(voucherBody.getStartDate());
        voucher.setEndDate(voucherBody.getEndDate());
        voucher.setUpdated_at(new Date());

//        voucher.update(voucher);
        Voucher updatedVoucher = voucherRepository.save(voucher);

        voucher.setStoreId(voucher.getStoreId());

//        // Check if the voucher type has changed from STORE to PLATFORM
//        if (voucherBody.getVoucherType() == VoucherType.PLATFORM) {
//            // Delete voucher_store data from the database if the type changed to PLATFORM
//            voucherStoreRepository.deleteByVoucherId(updatedVoucher.getId());
//        }
//
//        // Update or save data in voucher_store table based on the request
//        if (bodyVoucher.getVoucherType() == VoucherType.STORE) {
//            // Delete existing voucher_store data from the database
//            voucherStoreRepository.deleteByVoucherId(updatedVoucher.getId());
//            // Save the new voucher_store data from the request body
//            List<VoucherStore> voucherStoreList = bodyVoucher.getVoucherStoreList();
//            for (VoucherStore voucherStore : voucherStoreList) {
//                voucherStore.setVoucherId(updatedVoucher.getId());
//                voucherStoreRepository.save(voucherStore);
//            }
//        }

        // Delete data from DB
        voucherServiceTypeRepository.deleteByVoucherId(updatedVoucher.getId());

        // Save to voucher_service_type table
        // Create and save the DINEIN VoucherServiceType
        VoucherServiceType dineInServiceType = new VoucherServiceType();
        dineInServiceType.setVoucherId(id);
        dineInServiceType.setServiceType("DINEIN");
        voucherServiceTypeRepository.save(dineInServiceType);

        // Create and save the DELIVERIN VoucherServiceType
        VoucherServiceType deliverInServiceType = new VoucherServiceType();
        deliverInServiceType.setVoucherId(id);
        deliverInServiceType.setServiceType("DELIVERIN");
        voucherServiceTypeRepository.save(deliverInServiceType);

        if (!voucherBody.getVoucherTerms().isEmpty()) {
            // Delete data from DB
            voucherTermsRepository.deleteByVoucherId(updatedVoucher.getId());
            // Save to voucher_terms table
            for (ProductVoucherTermsRequest voucherTermsRequest: voucherBody.getVoucherTerms()) {
                VoucherTerms voucherTerms = new VoucherTerms();

                voucherTerms.setVoucherId(id);
                voucherTerms.setTerms(voucherTermsRequest.getTerms());
                voucherTermsRepository.save(voucherTerms);
            }
        }

        // Delete data from DB
        voucherVerticalRepository.deleteByVoucherId(updatedVoucher.getId());
        // Save to voucher_vertical table
        VoucherVertical voucherVerticalToSave = new VoucherVertical();

        voucherVerticalToSave.setVoucherId(id);
        voucherVerticalToSave.setVerticalCode(voucherBody.getVerticalCode());
        voucherVerticalRepository.save(voucherVerticalToSave);

        int newVoucherQuantity = voucherBody.getTotalQuantity();
        if (newVoucherQuantity > oldVoucherQuantity){
            int moreVoucherQuantity = newVoucherQuantity - oldVoucherQuantity;
            for (int i = 0; i < moreVoucherQuantity; i++) {
                VoucherSerialNumber voucherSerialNumber = new VoucherSerialNumber(); // Create a new instance inside the loop

                voucherSerialNumber.setVoucherId(updatedVoucher.getId());
                voucherSerialNumber.setExpiryDate(updatedVoucher.getEndDate());
                voucherSerialNumber.setIsUsed(false);
                voucherSerialNumber.setCurrentStatus(VoucherCurrentStatus.NEW);
                VoucherSerialNumber savedVoucherSerialNumber =  voucherSerialNumberRepository.save(voucherSerialNumber);

                voucherSerialNumber.setVoucherRedeemCode(VoucherSerialNumber.generateUniqueRedeemCode(voucherBody.getName(), voucherSerialNumber.getId()));
                String voucherRedeemCode = savedVoucherSerialNumber.getVoucherRedeemCode();
                savedVoucherSerialNumber.setSerialNumber(VoucherSerialNumber.generateUniqueSerialNumber(voucherRedeemCode));

                voucherSerialNumber.update(savedVoucherSerialNumber);
                voucherSerialNumberRepository.save(voucherSerialNumber);
            }
        }

        Optional<Product> productOptional = productRepository.findByVoucherId(id);

        if (!productOptional.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                    logprefix, " NOT_FOUND voucher product with ID: " + id);
            response.setError("Voucher Product not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        List<ProductInventory> productInventoryOptional = productInventoryRepository.findByProductId(productOptional.get().getId());

        if (productInventoryOptional.size() == 0){
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND voucher product inventory with ID: " + id);
            response.setError("Voucher Product Inventory not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        String SkuGenerated = Product.generateSku(voucherBody.getName());

        // Product table update--------------------------
        // Create a new Product and Product Inventory entity based on the voucher information
        Product productForVoucher = productOptional.get();

        // Set the product name based on the voucher name
        productForVoucher.setName(voucherBody.getName());
        productForVoucher.setSeoName(SkuGenerated);
        productForVoucher.update(productForVoucher);
        Product savedProduct = productRepository.save(productForVoucher);

        // Product Inventory table update--------------------------
        for (ProductInventory pi :productInventoryOptional){

            pi.setPrice(voucherBody.getSellingPrice());
            pi.setDineInPrice(voucherBody.getSellingPrice());
            pi.setSKU(SkuGenerated);
            pi.setQuantity(voucherBody.getTotalQuantity());
            productInventoryRepository.save(pi);

        }
        // Refresh repositories to get the latest data
        voucherRepository.refresh(updatedVoucher);
        productRepository.refresh(savedProduct);
        Optional<Product> optionalProduct = productRepository.findById(savedProduct.getId());

        response.setData(optionalProduct.get());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


//    @PostMapping(path = {"/claim/{customerId}/{voucherCode}"}, name = "voucher-post")
//    @PreAuthorize("hasAnyAuthority('voucher-post', 'all')")
//    public ResponseEntity<HttpResponse> postCustomerClaimVoucher(HttpServletRequest request,
//                                                                 @PathVariable() String customerId,
//                                                                 @PathVariable() String voucherCode
//    ) {
//        String logprefix = request.getRequestURI();
//        HttpResponse response = new HttpResponse(request.getRequestURI());
//
//        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "customerId: " + customerId + " voucherCode:" + voucherCode);
//
//
//        //check promo code
//        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode);
//        if (voucher == null) {
//            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND customerId: " + customerId);
//            response.setStatus(HttpStatus.NOT_FOUND);
//            response.setError("Voucher not found");
//            response.setMessage("Voucher not found");
//            return ResponseEntity.status(response.getStatus()).body(response);
//        } else {
//            //check status
//            if (voucher.getStatus() != VoucherStatus.ACTIVE) {
//                response.setStatus(HttpStatus.EXPECTATION_FAILED);
//                response.setError("Voucher not active");
//                response.setMessage("Voucher not active");
//                return ResponseEntity.status(response.getStatus()).body(response);
//            }
//            //check total redeem
//            if (voucher.getTotalRedeem() >= voucher.getTotalQuantity()) {
//                response.setStatus(HttpStatus.EXPECTATION_FAILED);
//                response.setError("Voucher fully redeemed");
//                response.setMessage("Sorry, voucher is fully redeemed");
//                return ResponseEntity.status(response.getStatus()).body(response);
//            }
//            //check expiry date
//            Date currentDate = new Date();
//            if (currentDate.compareTo(voucher.getStartDate()) < 0 || currentDate.compareTo(voucher.getEndDate()) > 0) {
//                response.setStatus(HttpStatus.EXPECTATION_FAILED);
//                response.setError("Voucher is expired");
//                response.setMessage("Voucher is expired");
//                return ResponseEntity.status(response.getStatus()).body(response);
//            }
//        }
//        return ResponseEntity.status(response.getStatus()).body(response);
//    }
}
