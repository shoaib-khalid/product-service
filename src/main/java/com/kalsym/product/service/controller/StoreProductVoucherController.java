package com.kalsym.product.service.controller;

//Importing Models
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.VoucherCurrentStatus;
import com.kalsym.product.service.enums.VoucherGroupType;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.store.*;
//Importing Enums
import com.kalsym.product.service.enums.VoucherStatus;
import com.kalsym.product.service.enums.VoucherType;
//Importing Repositories
import com.kalsym.product.service.repository.*;
//Importing Utilities
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;

//Importing Java Utils
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

//Importing Swagger
//Importing Spring framework
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;

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
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "voucherType:" + voucherType + " storeId:" + storeId);

        Voucher voucherMatch = new Voucher();

        Pageable pageable = PageRequest.of(page, pageSize);
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<Voucher> example = Example.of(voucherMatch, matcher);

        Specification<Voucher> voucherSpec = VoucherSearchSpecs.getSpecWithDatesBetween(new Date(), voucherType, storeId, verticalCode, voucherCode, parseVoucherStatus(voucherStatus), example);
        Page<Voucher> voucherWithPage = voucherRepository.findAll(voucherSpec, pageable);

        response.setStatus(HttpStatus.OK);
        response.setData(voucherWithPage);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private VoucherStatus parseVoucherStatus(String voucherStatus) {
        if (voucherStatus != null) {
            try {
                return VoucherStatus.valueOf(voucherStatus);
            } catch (IllegalArgumentException e) {
                // To Do
                // Handle the case when the input voucherStatus does not match any enum constant
                System.out.println("Error in Voucher Status");
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
//            response.setErrorStatus(HttpStatus.NOT_FOUND);
            response.setError("Voucher not found");
            return ResponseEntity.status(response.getStatus()).body(response);

        }

        response.setStatus(HttpStatus.OK);
        response.setData(voucherOptional.get());

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ApiOperation(value = "Create voucher", notes = "Note: Include storeId for STORE voucher type.")
    @PostMapping(path = {"/create"}, name = "voucher-post")
    @PreAuthorize("hasAnyAuthority('voucher-post', 'all')")
    public ResponseEntity<HttpResponse> postVoucher(HttpServletRequest request,
                                                    @RequestParam() String storeId,
                                                    @RequestParam() String categoryId,
                                                    @Valid @RequestBody Voucher voucherBody) {
        String logprefix = "postVoucher()";
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "bodyProduct: " + voucherBody.toString());

        // Set total redeem to 0
        voucherBody.setTotalRedeem(0);

        if (storeId != null) {
            Optional<Store> optionalStore = storeRepository.findById(storeId);

            if (!optionalStore.isPresent()) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
//                response.setErrorStatus(HttpStatus.NOT_FOUND);
                response.setError("Store not found");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            // Set voucher type to STORE whenever has storeId
            voucherBody.setVoucherType(VoucherType.STORE);
            voucherBody.setStoreId(storeId);
        }
        if (categoryId != null) {
            Optional<StoreCategory> optionalStore = storeCategoryRepository.findById(categoryId);

            if (!optionalStore.isPresent()) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
//                response.setErrorStatus(HttpStatus.NOT_FOUND);
                response.setError("Category not found");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }

        voucherBody.setGroupType(VoucherGroupType.CASH);
        Voucher savedVoucher = voucherRepository.save(voucherBody);

        // If type is STORE, save to voucher_store table
        if (savedVoucher.getVoucherType().equals(VoucherType.STORE) && !voucherBody.getVoucherStoreList().isEmpty()) {

            for (VoucherStore voucherStore: voucherBody.getVoucherStoreList()) {
                voucherStore.setVoucherId((savedVoucher.getId()));
                voucherStore.setStoreId(storeId);
                voucherStoreRepository.save(voucherStore);
            }
        }
        // Save to voucher_service_type table
        if (!voucherBody.getVoucherServiceTypeList().isEmpty()) {
            for (VoucherServiceType voucherServiceType: voucherBody.getVoucherServiceTypeList()) {
                voucherServiceType.setVoucherId(savedVoucher.getId());
                voucherServiceTypeRepository.save(voucherServiceType);
            }
        }

        // Save to voucher_terms table
        if (!voucherBody.getVoucherTerms().isEmpty()) {
            for (VoucherTerms voucherTerms: voucherBody.getVoucherTerms()) {
                voucherTerms.setVoucherId(savedVoucher.getId());
                voucherTermsRepository.save(voucherTerms);
            }
        }

        // Save to voucher_vertical table
        if (!voucherBody.getVoucherVerticalList().isEmpty()) {
            for (VoucherVertical voucherVertical: voucherBody.getVoucherVerticalList()) {
                voucherVertical.setVoucherId(savedVoucher.getId());
                voucherVerticalRepository.save(voucherVertical);
            }
        }

        if (!voucherBody.getVoucherSerialNumber().isEmpty()) {
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
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("Product name already exists");
                response.setData(errors);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }
        // Set the voucher id in the product table
        productForVoucher.setVoucherId(voucherBody.getId());
        // Set the category id in the product table
        productForVoucher.setCategoryId(categoryId);
        // Set the voucher's storeId in Product table
        productForVoucher.setStoreId(voucherBody.getStoreId());
        // Set the specific value for 'allowOutOfStockPurchases' to false
        productForVoucher.setAllowOutOfStockPurchases(false);
        // Set the custom price as false
        productForVoucher.setCustomPrice(false);
        // Set track quantity as false
        productForVoucher.setTrackQuantity(false);
        // set allow out of stock option as false
        productForVoucher.setAllowOutOfStockPurchases(false);
        //set minimum quantity for alarm to 10
        productForVoucher.setMinQuantityForAlarm(10);

        // bodyProduct.setSeoName(seoName);
        if (productForVoucher.getIsPackage()==null) { productForVoucher.setIsPackage(Boolean.FALSE); }

        //to handle backward compatibility since we implement new features for add on
        if(productForVoucher.getHasAddOn()==null) { productForVoucher.setHasAddOn(Boolean.FALSE);}

        //Save in Repository
        Product savedProduct = productRepository.save(productForVoucher);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product added to store with storeId: {}, productId: {}" + storeId, savedProduct.getId());


        // Product table update--------------------------
        // set product id
        productForInventory.setProductId(productForVoucher.getId());
        productForInventory.setItemCode(productForVoucher.getId());
        // set quantity from voucher body
        productForInventory.setQuantity(voucherBody.getTotalQuantity());

        if (productForInventory.getCostPrice()==null) {
            productForInventory.setCostPrice(0.00);
        }
        // if new client for delivery, we auto set the dine in price reduce 15%
        if (productForInventory.getDineInPrice()==null) {
            productForInventory.setDineInPrice(productForInventory.getCostPrice()*0.85);
        }

        //Save in Repository
        productInventoryRepository.save(productForInventory);

        response.setStatus(HttpStatus.CREATED);
        response.setData(savedVoucher);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PutMapping(path = {"/edit/{id}"}, name = "voucher-put")
    @PreAuthorize("hasAnyAuthority('voucher-put', 'all')")
    public ResponseEntity<HttpResponse> putVoucher(HttpServletRequest request,
                                                   @PathVariable String id,
                                                   @RequestBody Voucher bodyVoucher) {
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
        voucher.update(bodyVoucher);
        Voucher updatedVoucher = voucherRepository.save(voucher);

        voucher.setStoreId(voucher.getStoreId());

        // Check if the voucher type has changed from STORE to PLATFORM
        if (bodyVoucher.getVoucherType() == VoucherType.PLATFORM) {
            // Delete voucher_store data from the database if the type changed to PLATFORM
            voucherStoreRepository.deleteByVoucherId(updatedVoucher.getId());
        }

        // Update or save data in voucher_store table based on the request
        if (bodyVoucher.getVoucherType() == VoucherType.STORE) {
            // Delete existing voucher_store data from the database
            voucherStoreRepository.deleteByVoucherId(updatedVoucher.getId());
            // Save the new voucher_store data from the request body
            List<VoucherStore> voucherStoreList = bodyVoucher.getVoucherStoreList();
            for (VoucherStore voucherStore : voucherStoreList) {
                voucherStore.setVoucherId(updatedVoucher.getId());
                voucherStoreRepository.save(voucherStore);
            }
        }

        // If exist
        if (!bodyVoucher.getVoucherServiceTypeList().isEmpty()) {
            // Delete data from DB
            voucherServiceTypeRepository.deleteByVoucherId(updatedVoucher.getId());
            // Save to voucher_service_type table
            for (VoucherServiceType voucherServiceType: bodyVoucher.getVoucherServiceTypeList()) {
                voucherServiceType.setVoucherId(updatedVoucher.getId());
                voucherServiceTypeRepository.save(voucherServiceType);
            }
        }

        if (!bodyVoucher.getVoucherTerms().isEmpty()) {
            // Delete data from DB
            voucherTermsRepository.deleteByVoucherId(updatedVoucher.getId());
            // Save to voucher_terms table
            for (VoucherTerms voucherTerms: bodyVoucher.getVoucherTerms()) {
                voucherTerms.setVoucherId(updatedVoucher.getId());
                voucherTermsRepository.save(voucherTerms);
            }
        }

        if (!bodyVoucher.getVoucherVerticalList().isEmpty()) {
            // Delete data from DB
            voucherVerticalRepository.deleteByVoucherId(updatedVoucher.getId());
            // Save to voucher_vertical table
            for (VoucherVertical voucherVertical: bodyVoucher.getVoucherVerticalList()) {
                voucherVertical.setVoucherId(updatedVoucher.getId());
                voucherVerticalRepository.save(voucherVertical);
            }
        }

        int newVoucherQuantity = bodyVoucher.getTotalQuantity();
        if(newVoucherQuantity > oldVoucherQuantity){
            int moreVoucherQuantity = newVoucherQuantity - oldVoucherQuantity;
            for (int i = 0; i < moreVoucherQuantity; i++) {
                VoucherSerialNumber voucherSerialNumber = new VoucherSerialNumber(); // Create a new instance inside the loop

                voucherSerialNumber.setVoucherId(updatedVoucher.getId());
                voucherSerialNumber.setExpiryDate(updatedVoucher.getEndDate());
                voucherSerialNumber.setIsUsed(false);
                voucherSerialNumber.setCurrentStatus(VoucherCurrentStatus.NEW);
                VoucherSerialNumber savedVoucherSerialNumber =  voucherSerialNumberRepository.save(voucherSerialNumber);

                voucherSerialNumber.setVoucherRedeemCode(VoucherSerialNumber.generateUniqueRedeemCode(bodyVoucher.getName(), voucherSerialNumber.getId()));
                String voucherRedeemCode = savedVoucherSerialNumber.getVoucherRedeemCode();
                savedVoucherSerialNumber.setSerialNumber(VoucherSerialNumber.generateUniqueSerialNumber(voucherRedeemCode));

                voucherSerialNumber.update(savedVoucherSerialNumber);
                voucherSerialNumberRepository.save(voucherSerialNumber);
            }
        }

        if (!oldName.equals(updatedVoucher.getName())) {

            Optional<Product> productOptional = productRepository.findByVoucherId(id);

            if (!productOptional.isPresent()) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                        logprefix, " NOT_FOUND voucher product with ID: " + id);
                response.setError("Voucher Product not found");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            Optional<ProductInventory> productInventoryOptional = productInventoryRepository.findById(productOptional.get().getId());
            if (!productInventoryOptional.isPresent()) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION,
                        logprefix, " NOT_FOUND voucher product inventory with ID: " + id);
                response.setError("Voucher Product Inventory not found");
                return ResponseEntity.status(response.getStatus()).body(response);
            }

            // Product table update--------------------------
            // Create a new Product and Product Inventory entity based on the voucher information
            Product productForVoucher = productOptional.get();
            ProductInventory productForInventory = productInventoryOptional.get();

            // Set the product name based on the voucher name
            productForVoucher.setName(bodyVoucher.getName());

            productForVoucher.update(productForVoucher);
            productRepository.save(productForVoucher);

            // Product Inventory table update--------------------------
            // Set quantity from voucher body
            productForInventory.setQuantity(bodyVoucher.getTotalQuantity());

            if (productForInventory.getCostPrice() == null) {
                productForInventory.setCostPrice(0.00);
            }

            // if new client for delivery, we auto set the dine in price reduce 15%
            if (productForInventory.getDineInPrice() == null) {
                productForInventory.setDineInPrice(productForInventory.getCostPrice() * 0.85);
            }

            // Save in Repository
            productInventoryRepository.save(productForInventory);
        }
        response.setData(voucherRepository.findById(id));
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
