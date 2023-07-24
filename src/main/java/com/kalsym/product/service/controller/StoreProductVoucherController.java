package com.kalsym.product.service.controller;

//Importing Models
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.product.Product;
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
    StoreRepository storeRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreDiscountRepository storeDiscountRepository;

    @Autowired
    StoreDiscountProductRepository storeDiscountProductRepository;


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
                                                    @RequestParam(required = false) String storeId,
                                                    @Valid @RequestBody Voucher voucherBody) {

        String logprefix = "postVoucher()";
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "bodyProduct: " + voucherBody.toString());

        // Set voucher type to PLATFORM as default
        voucherBody.setVoucherType(VoucherType.PLATFORM);

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
        }
        Voucher savedVoucher = voucherRepository.save(voucherBody);

        // If type is STORE, save to voucher_store table
        if (savedVoucher.getVoucherType().equals(VoucherType.STORE) && !voucherBody.getVoucherStoreList().isEmpty()) {
            for (VoucherStore voucherStore: voucherBody.getVoucherStoreList()) {
                voucherStore.setVoucherId((savedVoucher.getId()));

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


        // Create a new Product entity based on the voucher information
        Product productForVoucher = new Product();

        // Set the product name based on the voucher name
        productForVoucher.setName(voucherBody.getName());
        // Set the voucher id in the product table
        productForVoucher.setVoucherId(voucherBody.getId());
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


        Product savedProduct = productRepository.save(productForVoucher);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product added to store with storeId: {}, productId: {}" + storeId, savedProduct.getId());

        response.setStatus(HttpStatus.CREATED);
        response.setData(savedVoucher + "  " + savedProduct);
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

        response.setData(voucherRepository.findById(id));
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/verify/{customerEmail}/{voucherCode}/{storeId}"}, name = "voucher-post")
    @PreAuthorize("hasAnyAuthority('voucher-post', 'all')")
    public ResponseEntity<HttpResponse> postGuestClaimVoucher(HttpServletRequest request,
                                                              @PathVariable() String customerEmail,
                                                              @PathVariable() String voucherCode,
                                                              @PathVariable(required = false) String storeId
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "customerEmail: " + customerEmail + " voucherCode:" + voucherCode);

//        List<Customer> optCustomer = null;
//        if (storeId!=null) {
//            optCustomer = customerRepository.findByEmailAndStoreId(customerEmail, storeId);
//        } else {
//            optCustomer = customerRepository.findByEmail(customerEmail);
//        }

        //check promo code
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode);
        if (voucher == null) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND voucherCode: " + voucherCode);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("Voucher not found");
            response.setMessage("Voucher not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            //check status
            if (voucher.getStatus() != VoucherStatus.ACTIVE) {
                response.setStatus(HttpStatus.EXPECTATION_FAILED);
                response.setError("Voucher not active");
                response.setMessage("Voucher not active");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            //check total redeem
            if (voucher.getTotalRedeem() >= voucher.getTotalQuantity()) {
                response.setStatus(HttpStatus.EXPECTATION_FAILED);
                response.setError("Voucher fully redeemed");
                response.setMessage("Sorry, you have redeemed this voucher");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            //check expiry date
            Date currentDate = new Date();
            if (currentDate.compareTo(voucher.getStartDate()) < 0 || currentDate.compareTo(voucher.getEndDate()) > 0) {
                response.setStatus(HttpStatus.EXPECTATION_FAILED);
                response.setError("Voucher is expired");
                response.setMessage("Voucher is expired");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }

        if (storeId != null && voucher.getVoucherType() == VoucherType.STORE) {
            //check store id
            boolean storeValid = false;
            List<VoucherStore> storeList = voucher.getVoucherStoreList();
            for (int x = 0; x < storeList.size(); x++) {
                if (storeId.equals(((List<?>) storeList).get(x))) { //get Storeid was removed
                    storeValid = true;
                    break;
                }
            }
            if (!storeValid) {
                Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Voucher not valid for this store storeId: " + storeId + " voucherId:" + voucher.getId());
                response.setStatus(HttpStatus.CONFLICT);
                response.setError("Voucher not valid for this store");
                response.setMessage("Sorry, voucher code cannot be used for this store");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }

        response.setStatus(HttpStatus.OK);
        response.setData(voucher);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PostMapping(path = {"/claim/{customerId}/{voucherCode}"}, name = "voucher-post")
    @PreAuthorize("hasAnyAuthority('voucher-post', 'all')")
    public ResponseEntity<HttpResponse> postCustomerClaimVoucher(HttpServletRequest request,
                                                                 @PathVariable() String customerId,
                                                                 @PathVariable() String voucherCode
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "customerId: " + customerId + " voucherCode:" + voucherCode);


        //check promo code
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode);
        if (voucher == null) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND customerId: " + customerId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("Voucher not found");
            response.setMessage("Voucher not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            //check status
            if (voucher.getStatus() != VoucherStatus.ACTIVE) {
                response.setStatus(HttpStatus.EXPECTATION_FAILED);
                response.setError("Voucher not active");
                response.setMessage("Voucher not active");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            //check total redeem
            if (voucher.getTotalRedeem() >= voucher.getTotalQuantity()) {
                response.setStatus(HttpStatus.EXPECTATION_FAILED);
                response.setError("Voucher fully redeemed");
                response.setMessage("Sorry, voucher is fully redeemed");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            //check expiry date
            Date currentDate = new Date();
            if (currentDate.compareTo(voucher.getStartDate()) < 0 || currentDate.compareTo(voucher.getEndDate()) > 0) {
                response.setStatus(HttpStatus.EXPECTATION_FAILED);
                response.setError("Voucher is expired");
                response.setMessage("Voucher is expired");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
