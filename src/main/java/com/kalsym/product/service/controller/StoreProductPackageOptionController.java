package com.kalsym.product.service.controller;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductPackageOption;
import com.kalsym.product.service.model.product.ProductPackageOptionDetail;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.ProductPackageOptionRepository;
import com.kalsym.product.service.repository.ProductPackageOptionDetailRepository;
import com.kalsym.product.service.service.FileStorageService;
import com.kalsym.product.service.utility.Logger;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/package/{packageId}/options")
public class StoreProductPackageOptionController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductPackageOptionRepository productPackageOptionRepository;
    
    @Autowired
    ProductPackageOptionDetailRepository productPackageOptionDetailRepository;
    
    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    StoreRepository storeRepository;

    @GetMapping(path = {""}, name = "store-package-options-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-package-options-get', 'all')")
    public ResponseEntity<HttpResponse> getStorePackageOptions(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String packageId) {
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

        Optional<Product> optProdcut = productRepository.findById(packageId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "package NOT_FOUND packageId: " + packageId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("package not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(productPackageOptionRepository.findByPackageId(packageId));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "store-package-options-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-package-options-get', 'all')")
    public ResponseEntity<HttpResponse> getStorePackageOptionsById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String packageId,
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

        Optional<Product> optProdcut = productRepository.findById(packageId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "package NOT_FOUND packageId: " + packageId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("package not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<ProductPackageOption> optProductPackage = productPackageOptionRepository.findById(id);

        if (!optProductPackage.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "package NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("package not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(optProductPackage.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-package-options-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-package-options-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> deleteStorePackageOptionsById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String packageId,
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

        Optional<Product> optProdcut = productRepository.findById(packageId);

        if (!optProdcut.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "package NOT_FOUND packageId: " + packageId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("package not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<ProductPackageOption> optProductPackageOption = productPackageOptionRepository.findById(id);

        if (!optProductPackageOption.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "package option NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("package option not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        productPackageOptionRepository.delete(optProductPackageOption.get());

        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "store-package-options-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-package-options-put-by-id', 'all')")
    public ResponseEntity<HttpResponse> putStorePackageOptionsById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String packageId,
            @PathVariable String id,
            @RequestBody ProductPackageOption productPackageBody) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(packageId);

        if (!optProdcut.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "package NOT_FOUND packageId: " + packageId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("package not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND packageId: " + packageId);

        Optional<ProductPackageOption> optProductPackage = productPackageOptionRepository.findById(id);

        if (!optProductPackage.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "option NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("package option not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND package option Id: " + id);

        ProductPackageOption packageOption = optProductPackage.get();
        packageOption.update(productPackageBody);

        packageOption = productPackageOptionRepository.save(packageOption);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "saved package option id: " + packageOption.getId());
        
        if (productPackageBody.getProductPackageOptionDetail()!=null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Update option product list");
            productPackageOptionDetailRepository.deleteByProductPackageOptionId(packageOption.getId());
            for (int i=0;i<productPackageBody.getProductPackageOptionDetail().size();i++) {
                ProductPackageOptionDetail product = productPackageBody.getProductPackageOptionDetail().get(i);
                product.setProductPackageOptionId(packageOption.getId());
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Saving option detail optionId:"+product.getProductPackageOptionId()+" productId:"+product.getProductId());
                productPackageOptionDetailRepository.save(product);
            }
        }
        
        //query back whole object to response all updated data
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "retrieve updated package option id: " + packageOption.getId());
        Optional<ProductPackageOption> optProductPackageUpdated = productPackageOptionRepository.findById(packageOption.getId());
        ProductPackageOption updatedPackageOption = null;
        if (optProductPackageUpdated.isPresent()) {
            updatedPackageOption = optProductPackageUpdated.get();
            for (int i=0;i<updatedPackageOption.getProductPackageOptionDetail().size();i++) {
                ProductPackageOptionDetail optionDetails = updatedPackageOption.getProductPackageOptionDetail().get(i);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Option details Id:"+optionDetails.getId()+" productId:"+optionDetails.getProductId());
                if (optionDetails.getProduct()==null) {
                    Optional<Product> optProduct = productRepository.findById(optionDetails.getProductId());
                    if (optProduct.isPresent()) {
                        optionDetails.setProduct(optProduct.get());
                    }
                }
                Product product = optionDetails.getProduct();
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Product details Id:"+product.getId()+" productId:"+product.getName());                
            }
        }
        response.setStatus(HttpStatus.OK);
        response.setData(updatedPackageOption);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "store-package-options-post")
    @PreAuthorize("hasAnyAuthority('store-package-options-post', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStorePackageOption(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String packageId,
            @RequestBody ProductPackageOption productPackageOptionBody) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        Optional<Product> optProdcut = productRepository.findById(packageId);

        if (!optProdcut.isPresent()) {
            Logger.application.warn(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "package NOT_FOUND packageId: " + packageId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("packageId not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "FOUND packageId: " + packageId);

        ProductPackageOption productPackageOption = productPackageOptionRepository.save(productPackageOptionBody);
        for (int i=0;i<productPackageOptionBody.getProductPackageOptionDetail().size();i++) {
            ProductPackageOptionDetail product = productPackageOptionBody.getProductPackageOptionDetail().get(i);
            product.setProductPackageOptionId(productPackageOption.getId());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Saving option detail optionId:"+product.getProductPackageOptionId()+" productId:"+product.getProductId());
            productPackageOptionDetailRepository.save(product);
        }
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "saved product package id : " + productPackageOption.getId());

        //query back whole object to response all updated data
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "retrieve inserted package option id: " + productPackageOption.getId());
        Optional<ProductPackageOption> optProductPackageUpdated = productPackageOptionRepository.findById(productPackageOption.getId());
        ProductPackageOption updatedPackageOption = null;
        if (optProductPackageUpdated.isPresent()) {
            updatedPackageOption = optProductPackageUpdated.get();
            for (int i=0;i<updatedPackageOption.getProductPackageOptionDetail().size();i++) {
                ProductPackageOptionDetail optionDetails = updatedPackageOption.getProductPackageOptionDetail().get(i);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Option details Id:"+optionDetails.getId()+" productId:"+optionDetails.getProductId());
                if (optionDetails.getProduct()==null) {
                    Optional<Product> optProduct = productRepository.findById(optionDetails.getProductId());
                    if (optProduct.isPresent()) {
                        optionDetails.setProduct(optProduct.get());
                    }
                }
                Product product = optionDetails.getProduct();
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Product details Id:"+product.getId()+" productId:"+product.getName());                
            }
        }
        response.setStatus(HttpStatus.OK);
        response.setData(updatedPackageOption);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
