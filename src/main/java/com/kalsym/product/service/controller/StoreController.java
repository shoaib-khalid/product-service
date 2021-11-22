package com.kalsym.product.service.controller;

import com.kalsym.product.service.service.StoreSubdomainHandler;
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.MySQLUserDetails;
import com.kalsym.product.service.model.RegionVertical;
import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.RegionVerticalRepository;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Validation;
import com.kalsym.product.service.utility.SessionInformation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.StoreWithDetails;
import com.kalsym.product.service.model.store.StoreCommission;
import com.kalsym.product.service.model.store.Client;

import com.kalsym.product.service.model.livechatgroup.StoreCreationResponse;
import com.kalsym.product.service.model.store.StoreAsset;
import com.kalsym.product.service.repository.StoreCategoryRepository;
import com.kalsym.product.service.repository.StoreWithDetailsRepository;
import com.kalsym.product.service.repository.StoreCommissionRepository;
import com.kalsym.product.service.repository.StoreAssetRepository;
import com.kalsym.product.service.repository.ClientsRepository;

import com.kalsym.product.service.service.StoreLiveChatService;
import com.kalsym.product.service.utility.Logger;
import com.kalsym.product.service.service.WhatsappService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7cu
 *
 * GET /stores GET /stores/{id}
 *
 * POST /stores
 *
 * DELETE /stores/{id}
 *
 * PUT /stores/{id}
 *
 *
 *
 *
 * GET /stores/{storeId}/products
 *
 * POST /stores/{storeId}/products
 *
 * GET /stores/{storeId}/store-categories
 *
 * POST /stores/{storeId}/store-categories
 */
@RestController()
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreWithDetailsRepository storeWithDetailsRepository;
    
    @Autowired
    ClientsRepository clientRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Autowired
    StoreSubdomainHandler storeSubdomainHandler;

    @Autowired
    StoreLiveChatService storeLiveChatService;
    
    @Autowired
    WhatsappService whatsappService;
    
    @Value("${storeCommission.minChargeAmount:1.5}")
    private Double minChargeAmount;
    @Value("${storeCommission.rate:3.5}")
    private Double rate;
    
    @Value("${store.logo.default.url:https://symplified.ai/store-assets/logo_symplified_bg.png}")
    private String storeLogoDefaultUrl;
    
    @Value("${store.banner.ecommerce.default.url:https://symplified.ai/store-assets/banner-ecomm.jpeg}")
    private String storeBannerEcommerceDefaultUrl;
    
    @Value("${store.banner.fnb.default.url:https://symplified.ai/store-assets/banner-fnb.png}")
    private String storeBannerFnbDefaultUrl;  
    
    @Value("${store.description.length:300}")
    private Integer storeDescriptionLength;

    @Value("${client.default.password:kalsym@123}")
    private String clientDefaultPassword;
    
    @Autowired
    StoreCommissionRepository storeComisssionRepository;
    
    @Autowired
    StoreAssetRepository storeAssetRepository;
    
    @Autowired
    private PasswordEncoder bcryptEncoder;
    
    @Autowired
    RegionVerticalRepository regionVerticalRepository;
    
    @GetMapping(path = {""}, name = "stores-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-get', 'all')")
    public ResponseEntity<HttpResponse> getStore(HttpServletRequest request,
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String verticalCode,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false, defaultValue = "name") String sortByCol,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        try {            
            
            StoreWithDetails store = new StoreWithDetails();
            
            MySQLUserDetails mysqlUserDetails = SessionInformation.getSessionInfo(logprefix);            
            if (mysqlUserDetails.getIsSuperUser())
                store.setClientId(null);
            else 
                store.setClientId(clientId);            
                        
            store.setCity(city);
            store.setName(name);
            store.setVerticalCode(verticalCode);
            store.setRegionCountry(null);
            store.setDomain(domain);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store: " + store, "");

            ExampleMatcher matcher = ExampleMatcher
                    .matchingAll()
                    .withIgnoreCase()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
            Example<StoreWithDetails> example = Example.of(store, matcher);

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
            Pageable pageable = PageRequest.of(page, pageSize);
            if (sortingOrder==Sort.Direction.ASC)
                pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
            else if (sortingOrder==Sort.Direction.DESC)
                pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
            Page<StoreWithDetails> fetchedPage = storeWithDetailsRepository.findAll(example, pageable);
            //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "elements: " + fetchedPage.getTotalElements(), "");
            //Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "element 0: " + fetchedPage.iterator().next(), "");

            response.setData(fetchedPage);
            response.setStatus(HttpStatus.OK);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Error fetching stores", "", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setError(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @GetMapping(path = {"/{id}"}, name = "stores-get-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-get-by-id', 'all')")
    public ResponseEntity<HttpResponse> getStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<StoreWithDetails> optStore = storeWithDetailsRepository.findById(id);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
       
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        response.setData(optStore.get());
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {""}, name = "stores-post")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postStore(HttpServletRequest request,
            @Valid @RequestBody Store bodyStore) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store: " + bodyStore.toString(), "");
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "minChargeAmount: " + minChargeAmount, "");
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "rate: " + rate, "");

        response.setStatus(HttpStatus.CREATED);
        Store savedStore = null;
        List<Store> stores = storeRepository.findAll();

        List<String> errors = new ArrayList<>();

        for (Store store : stores) {
            if (store.getName().equals(bodyStore.getName())) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store name already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("store name already exists");
                response.setData(errors);
                return ResponseEntity.status(response.getStatus()).body(response);
            }

            if (store.getDomain() != null && store.getDomain().equals(bodyStore.getDomain())) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store domain already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("store domain already exists");
                response.setData(errors);
                return ResponseEntity.status(response.getStatus()).body(response);
            }

        }

        try {
            //limit store desription to 100 characters
            if (bodyStore.getStoreDescription().length()>storeDescriptionLength) {
                String shortDescription = bodyStore.getStoreDescription().substring(0, storeDescriptionLength);
                bodyStore.setStoreDescription(shortDescription);
            }
            
            if (bodyStore.getIsBranch()==null) {
                bodyStore.setIsBranch(false);
            }
            if (bodyStore.getIsBranch()==false) {
                //only create domain for non-branch store
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "create store domain for non-branch", "");
                
                //customer will enter domain
                String baseDomain = "";
                Optional<RegionVertical> regionVertical = regionVerticalRepository.findById(bodyStore.getVerticalCode());
                if (regionVertical.isPresent()) {
                    baseDomain = regionVertical.get().getDomain();
                }
                
                //skip create domain in godaddy & nginx
                //String domain = storeSubdomainHandler.createSubDomain(bodyStore.getDomain(), bodyStore.getVerticalCode(), baseDomain);
                 
                //String domain = bodyStore.getDomain()+ "." + baseDomain;
                String domain = bodyStore.getDomain();
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "verticalCode:"+bodyStore.getVerticalCode()+" domain: " + domain, "");
               
                if (domain != null) {
                    bodyStore.setDomain(domain);
                    savedStore = storeRepository.save(bodyStore);
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store created with id: " + savedStore.getId(), "");
                } else {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "domain could not be created", "");
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setError("domain could not be created");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
  
                StoreCreationResponse scrCsr = storeLiveChatService.createGroup(domain + "-csr");

                if (scrCsr == null) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "csr group could not created", "");
                    storeRepository.delete(savedStore);
                    Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group could not be created", "");
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setError("store group could not be created");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                } else {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "csr group id: " + scrCsr.get_id(), "");
                    savedStore.setLiveChatCsrGroupId(scrCsr.get_id());
                    savedStore.setLiveChatCsrGroupName(domain + "-csr");
                    storeRepository.save(savedStore);
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group created", "");

                }
            
           
                StoreCreationResponse scrOrders = storeLiveChatService.createGroup(domain + "-orders");

                if (scrOrders == null) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "orders group could not created", "");
                    storeLiveChatService.deleteGroup(scrOrders.get_id());
                    storeRepository.delete(savedStore);
                    Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group could not be created", "");
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setError("store group could not be created");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                } else {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "orders group id: " + scrOrders.get_id(), "");
                    savedStore.setLiveChatOrdersGroupId(scrOrders.get_id());
                    savedStore.setLiveChatOrdersGroupName(domain + "-orders");
                    storeRepository.save(savedStore);
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group created", "");

                }
            
            } else {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "not create store domain for branch", "");
                savedStore = storeRepository.save(bodyStore);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store created with id: " + savedStore.getId(), "");
            }

            StoreCommission sc = new StoreCommission();
            sc.setRate(rate);
            sc.setMinChargeAmount(minChargeAmount);
            sc.setStoreId(savedStore.getId());
            sc.setSettlementDays(5);
            storeComisssionRepository.save(sc);
            response.setData(savedStore);
            
            //set default store asset
            StoreAsset storeAsset = new StoreAsset();
            storeAsset.setStoreId(savedStore.getId());
            storeAsset.setLogoUrl(storeLogoDefaultUrl);
            if (savedStore.getVerticalCode()!=null) {                
                if (savedStore.getVerticalCode().toUpperCase().contains("FNB")) {
                    storeAsset.setBannerUrl(storeBannerFnbDefaultUrl);
                }
            } else {
                storeAsset.setBannerUrl(storeBannerEcommerceDefaultUrl);
            }
            storeAssetRepository.save(storeAsset);
            
            //send whatsapp notification to merchant
            Optional<Client> clientOpt = clientRepository.findById(savedStore.getClientId());
            Client client = clientOpt.get();
            String password = "xxxxx";
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Default password:"+clientDefaultPassword);
            if (bcryptEncoder.matches(clientDefaultPassword, client.getPassword())) {
                //still using default password
                password = clientDefaultPassword;
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Still using default password");
            } else {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Password already changed");
            }
            String[] recipients = {savedStore.getPhoneNumber()};            
            whatsappService.sendWhatsappMessage(recipients, client.getUsername(), password);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " error creating store ", "", e);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PutMapping(path = {"/{id}"}, name = "stores-put-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-put-by-id', 'all') and @customOwnerVerifier.VerifyStore(#id)")
    public ResponseEntity<HttpResponse> putStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id,
            @Valid @RequestBody Store bodyStore
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());
        String logprefix = request.getRequestURI();

        try {

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " bodyStore: " + bodyStore, "");

            Optional<Store> optStore = storeRepository.findById(id);

            if (!optStore.isPresent()) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
                response.setStatus(HttpStatus.NOT_FOUND);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
            
            if (!Validation.VerifyClientId(optStore.get().getClientId())) {
                Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Invalid clientId", "");
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setError("Unathorized storeId");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            //check if merchant change domain            
            if (bodyStore.getDomain() != null && !optStore.get().getDomain().equals(bodyStore.getDomain())) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "change store domain", "");
                //check if domain conflict with other store
                List<Store> storesList = storeRepository.findByDomain(bodyStore.getDomain());
                for (int i=0;i<storesList.size();i++) {
                    if (!storesList.get(i).getId().equals(id)) {
                        response.setStatus(HttpStatus.CONFLICT);
                        response.setData("store domain already exists");
                        return ResponseEntity.status(response.getStatus()).body(response);
                    }
                }                
            }
            
            Store store = optStore.get();
            
            //limit store desription to 100 characters
            if (bodyStore.getStoreDescription().length()>storeDescriptionLength) {
                String shortDescription = bodyStore.getStoreDescription().substring(0, storeDescriptionLength);
                bodyStore.setStoreDescription(shortDescription);
            }
            
            store.update(bodyStore);

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "updated store with id: " + id);
            response.setData(storeRepository.save(store));
            response.setStatus(HttpStatus.OK);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "error saving store " + id, e);
            //response.setData(storeRepository.save(store));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setError(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @DeleteMapping(path = {"/{id}"}, name = "stores-delete-by-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-delete-by-id', 'all') and @customOwnerVerifier.VerifyStore(#id)")
    public ResponseEntity<HttpResponse> deleteStoreById(HttpServletRequest request,
            @PathVariable(required = true) String id
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + id, "");

        Optional<Store> optStore = storeRepository.findById(id);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND id: " + id);
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        
        if (!Validation.VerifyClientId(optStore.get().getClientId())) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Invalid clientId", "");
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setError("Unathorized storeId");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        storeLiveChatService.deleteGroup(optStore.get().getLiveChatCsrGroupId());
        storeLiveChatService.deleteGroup(optStore.get().getLiveChatOrdersGroupId());

        storeRepository.deleteById(id);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "deleted store with id: " + id);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {"/{storeId}/store-categories"}, name = "store-categories-post-by-store-id")
    @PreAuthorize("hasAnyAuthority('store-categories-post-by-store-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> postStoreCategoryByStoreId(HttpServletRequest request,
            @PathVariable String storeId,
            @Valid @RequestBody StoreCategory bodyStoreCategory) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, bodyStoreCategory.toString(), "");

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);
        
        if (!Validation.VerifyClientId(optStore.get().getClientId())) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Invalid clientId", "");
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setError("Unathorized storeId");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        bodyStoreCategory.setStoreId(storeId);

        StoreCategory savedStoreCategory = storeCategoryRepository.save(bodyStoreCategory);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "product added to store with storeId: {}, productId: {}" + storeId, savedStoreCategory.getId());
        response.setStatus(HttpStatus.CREATED);

        response.setData(savedStoreCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = {"/{storeId}/store-categories"}, name = "store-categories-get-by-stores-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-categories-get-by-stores-id', 'all') and @customOwnerVerifier.VerifyStore(#storeId)")
    public ResponseEntity<HttpResponse> putStoreCategoryByStoreId(HttpServletRequest request,
            @PathVariable String storeId) {
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
        
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "store found for id: {}", storeId);

        List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(storeId);
        response.setStatus(HttpStatus.OK);
        response.setData(storeCategories);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/asset/{clientId}"}, name = "store-assets-get-by-client-id", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-assets-get', 'all')")
    public ResponseEntity<HttpResponse> getStoreAssetsByClientId(HttpServletRequest request,
            @PathVariable String clientId) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "clientId: " + clientId);

        List<Store> storeList = storeRepository.findByClientId(clientId);

        if (storeList==null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Store not found for clientId: " + clientId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND store "+storeList.size()+" for clientId: " + clientId);
        
        List<StoreAsset> storeAssetList = new ArrayList<StoreAsset>();
        for (int i=0;i<storeList.size();i++) {
            String storeId = storeList.get(i).getId();
            Optional<StoreAsset> optAsset = storeAssetRepository.findById(storeId);
            if (optAsset.isPresent()) {
                storeAssetList.add(optAsset.get());
            }
        }
        response.setStatus(HttpStatus.OK);
        response.setData(storeAssetList);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
    @GetMapping(path = {"/checkdomain"}, name = "stores-check-domain-availability", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-check-domain-availability', 'all')")
    public ResponseEntity<HttpResponse> checkDomainAvailability(HttpServletRequest request,
            @RequestParam(required = true) String domain
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + domain, "");

        Optional<StoreWithDetails> optStore = storeWithDetailsRepository.findByDomain(domain);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Domain: " + domain+" IS available");
            response.setStatus(HttpStatus.OK);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Domain: " + domain+" NOT available");
            response.setStatus(HttpStatus.CONFLICT);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
       
        
        
    }
    
    
    @GetMapping(path = {"/checkname"}, name = "stores-check-name-availability", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-check-name-availability', 'all')")
    public ResponseEntity<HttpResponse> checkNameAvailability(HttpServletRequest request,
            @RequestParam(required = true) String storeName
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + storeName, "");

        Optional<StoreWithDetails> optStore = storeWithDetailsRepository.findByName(storeName);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Name: " + storeName+" IS available");
            response.setStatus(HttpStatus.OK);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Name: " + storeName+" NOT available");
            response.setStatus(HttpStatus.CONFLICT);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
       
        
        
    }

}
