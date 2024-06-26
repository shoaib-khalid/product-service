package com.kalsym.product.service.controller;

import com.kalsym.product.service.service.StoreSubdomainHandler;
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.DiscountCalculationType;
import com.kalsym.product.service.model.ItemDiscount;
import com.kalsym.product.service.model.MySQLUserDetails;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.model.RegionVertical;
import com.kalsym.product.service.model.ReserveDomain;
import com.kalsym.product.service.model.StoreSnooze;
import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.RegionCountriesRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.utility.DateTimeUtil;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Validation;
import com.kalsym.product.service.utility.SessionInformation;
import com.kalsym.product.service.utility.QrCodeGenerator;
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
import org.springframework.web.multipart.MultipartFile;

import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.StoreWithDetails;
import com.kalsym.product.service.model.store.StoreCommission;
import com.kalsym.product.service.model.store.StoreTiming;
import com.kalsym.product.service.model.store.Client;

import com.kalsym.product.service.model.livechatgroup.StoreCreationResponse;
import com.kalsym.product.service.model.product.ProductInventoryWithDetails;
import com.kalsym.product.service.model.product.ProductWithDetails;
import com.kalsym.product.service.model.store.StoreAsset;
import com.kalsym.product.service.model.store.StoreAssets;
import com.kalsym.product.service.model.store.object.CustomPageable;
import com.kalsym.product.service.model.store.object.TopStore;
import com.kalsym.product.service.repository.StoreCategoryRepository;
import com.kalsym.product.service.repository.StoreWithDetailsRepository;
import com.kalsym.product.service.repository.StoreCommissionRepository;
import com.kalsym.product.service.repository.StoreAssetRepository;
import com.kalsym.product.service.repository.StoreAssetsRepository;
import com.kalsym.product.service.repository.ClientsRepository;
import com.kalsym.product.service.repository.RegionVerticalRepository;
import com.kalsym.product.service.repository.ReserveDomainRepository;
import com.kalsym.product.service.repository.StoreDeliveryPeriodsRepository;
import com.kalsym.product.service.service.FileStorageService;

import com.kalsym.product.service.service.StoreLiveChatService;
import com.kalsym.product.service.utility.Logger;
import com.kalsym.product.service.utility.MultipartImage;
import com.kalsym.product.service.service.WhatsappService;
import com.kalsym.product.service.service.DeliveryService;
import com.kalsym.product.service.utility.ProductDiscount;
import com.kalsym.product.service.utility.StoreAssetsUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
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
    ReserveDomainRepository reserveDomainRepository;

    @Autowired
    StoreSubdomainHandler storeSubdomainHandler;

    @Autowired
    StoreLiveChatService storeLiveChatService;
    
    @Autowired
    WhatsappService whatsappService;
    
    @Autowired
    FileStorageService fileStorageService;
    
    @Autowired
    DeliveryService deliveryService;

    @Autowired
    RegionCountriesRepository regionCountriesRepository;
            
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
    
    @Value("${store.assets.url:https://symplified.ai/store-assets}")
    private String storeAssetsBaseUrl;
    
    @Value("${store.description.length:300}")
    private Integer storeDescriptionLength;

    @Value("${client.default.password:kalsym@123}")
    private String clientDefaultPassword;
    
    @Autowired
    StoreCommissionRepository storeComisssionRepository;
    
    @Autowired
    StoreAssetRepository storeAssetRepository;
    
    @Autowired
    StoreAssetsRepository storeAssetsRepository;
    
    @Autowired
    StoreDeliveryPeriodsRepository storeDeliveryPeriodsRepository;
    
    @Autowired
    private PasswordEncoder bcryptEncoder;
    
    @Autowired
    RegionVerticalRepository regionVerticalRepository;
    
    @Value("${store.favicon.easydukan.default.url:https://symplified.it/store-assets/fav-icon-easydukan.png}")
    private String storeFavIconUrlEasydukan;
    
    @Value("${store.favicon.deliverin.default.url:https://symplified.it/store-assets/fav-icon-deliverin.png}")
    private String storeFavIconUrlDeliverin;
    
    @Value("${store.favicon.symplified.default.url:https://symplified.it/store-assets/fav-icon-symplified.png}")
    private String storeFavIconUrlSymplified;

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    private String storeLogoDefaultPath;
    private String storeBannerEcommerceDefaultPath;
    private String storeBannerFnbDefaultPath;
    private String storeFavIconEasydukanPath;
    private String storeFavIconDeliverinPath;
    private String storeFavIconSymplifiedPath;

    //https://newbedev.com/spring-boot-value-returns-always-null
    @PostConstruct
    public void postConstruct(){
        storeLogoDefaultPath = assetServiceUrl+"/store-assets/logo_symplified_bg.png";
        storeBannerEcommerceDefaultPath = assetServiceUrl+ "/store-assets/banner-ecomm.jpeg";
        storeBannerFnbDefaultPath = assetServiceUrl+"/store-assets/banner-fnb.png";
        storeFavIconEasydukanPath = assetServiceUrl+"/store-assets/fav-icon-easydukan.png";
        storeFavIconDeliverinPath = assetServiceUrl+"/store-assets/e-kedai-favicon-32x32.png";
        storeFavIconSymplifiedPath = assetServiceUrl+"/store-assets/fav-icon-symplified.png";

    }
    
    @GetMapping(path = {""}, name = "stores-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-get', 'all')")
    public ResponseEntity<HttpResponse> getStore(HttpServletRequest request,
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String[] verticalCode,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String regionCountryId,
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
            store.setDomain(domain);
            store.setRegionCountryId(regionCountryId);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store: " + store, "");

            ExampleMatcher matcher = ExampleMatcher
                    .matchingAll()
                    .withIgnoreCase()
                    .withMatcher("regionCountryId", new GenericPropertyMatcher().exact())
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
            Example<StoreWithDetails> storeExample = Example.of(store, matcher);

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
            Pageable pageable = PageRequest.of(page, pageSize);
            if (sortingOrder==Sort.Direction.ASC)
                pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
            else if (sortingOrder==Sort.Direction.DESC)
                pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
           
            Page<StoreWithDetails> fetchedPage = storeWithDetailsRepository.findAll(getStoreSpec(verticalCode, domain, storeExample), pageable);
           
            //variable involve to calculate store timing 
            String dayNames[] = new DateFormatSymbols().getWeekdays();  
            Calendar date = Calendar.getInstance();  
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm"); 

            //make it as default
            String timeZone = "UTC";
            //to set the timezone
            if(regionCountryId!= null){
                Optional<RegionCountry> regionCountrySearch = regionCountriesRepository.findById(regionCountryId);
                if (regionCountrySearch.isPresent()) {
                    timeZone = regionCountrySearch.get().getTimezone();
                }
                formatter.setTimeZone(TimeZone.getTimeZone(timeZone));

            }
         
            Date curr = new Date();  
            int   currentDayInteger = date.get(Calendar.DAY_OF_WEEK);
            //extract result to set default assets
            List<StoreWithDetails> storeList = fetchedPage.getContent();        
            StoreWithDetails[] storeWithDetailsList = new StoreWithDetails[storeList.size()];
            for (int x=0;x<storeList.size();x++) {

                if(timeZone.equals("UTC")){
                    Optional<RegionCountry> getRegiounContry = regionCountriesRepository.findById(storeList.get(x).getRegionCountryId());
                    if (getRegiounContry.isPresent()) {
                        timeZone = getRegiounContry.get().getTimezone();
                    }
                    formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
                }

                //format date from UTC to timeZone with format HH:mm
                String currentTime = formatter.format(curr);

                //check for item discount in hashmap
                StoreWithDetails storeWithDetails = storeList.get(x);
                List<StoreAssets> storeAssetsList = storeAssetsRepository.findByStoreId(storeWithDetails.getId());
                storeAssetsList = StoreAssetsUtility.SetDefaultAsset(storeWithDetails.getVerticalCode(), storeWithDetails.getId(), storeAssetsList,
                storeAssetsRepository, regionVerticalRepository, 
                storeBannerFnbDefaultPath, storeBannerEcommerceDefaultPath,
                storeLogoDefaultPath, 
                storeFavIconSymplifiedPath, storeFavIconDeliverinPath, storeFavIconEasydukanPath,assetServiceUrl);        
                storeWithDetails.setStoreAssets(storeAssetsList);

                StoreSnooze st = new StoreSnooze();

                int currentStoreData = x;
                if(storeList.get(x).getStoreTiming().size()>0){
                    
                    StoreTiming optStoreTiming = storeList.get(x).getStoreTiming().stream()
                    .filter((StoreTiming sti) -> sti.getDay().contains(dayNames[date.get(Calendar.DAY_OF_WEEK)].toUpperCase()))
                    .map((StoreTiming stt)-> {
                        if(stt.getIsOff()){
                            storeWithDetails.setIsOpen(false);
                            
                            //to handle outbound array of dayNames
                            int tomorrowDay = currentDayInteger+1>7?1:currentDayInteger+1;

                            StoreTiming optStoreTimingTomorrow =  storeList.get(currentStoreData).getStoreTiming().stream().filter((StoreTiming tomsti) -> tomsti.getDay().contains(dayNames[tomorrowDay].toUpperCase())).findFirst().get();

                            Boolean isFoundOtherDay = false;
                            String openDay = "";

                            if(!optStoreTimingTomorrow.getIsOff()){
                                storeWithDetails.setStoreTimingMessage("Please come back tomorrow at "+optStoreTimingTomorrow.getOpenTime());
                    
                            } else{
                    
                                int g = tomorrowDay;

                                while (g < dayNames.length && isFoundOtherDay==false ) {
                                    
                                    String daysThisWeek = dayNames[g];
                                    StoreTiming optStoreTimingOtherDay =  storeList.get(currentStoreData).getStoreTiming().stream().filter((StoreTiming otherDayStore) -> otherDayStore.getDay().contains(daysThisWeek.toUpperCase())).findFirst().get();

                                    if(!optStoreTimingOtherDay.getIsOff()){
                                     isFoundOtherDay = true;
                                     openDay = dayNames[g];
                                     storeWithDetails.setStoreTimingMessage("Please come back on "+openDay+" "+optStoreTimingOtherDay.getOpenTime());

                                    }

                    
                                  g++;
                                }
                                
                                if(openDay==""){
                    
                                    int m = 1;
                    
                                   while (m < currentDayInteger && isFoundOtherDay==false ) {
                                    String daysNextWeek = dayNames[m];

                                    StoreTiming optStoreTimeOtherDay =  storeList.get(currentStoreData).getStoreTiming().stream().filter((StoreTiming otherDayStore) -> otherDayStore.getDay().contains(daysNextWeek.toUpperCase())).findFirst().get();

                                        if(!optStoreTimeOtherDay.getIsOff()){
                                        isFoundOtherDay = true;
                                        openDay = dayNames[m];
                                        storeWithDetails.setStoreTimingMessage("Please come back on "+openDay+" "+optStoreTimeOtherDay.getOpenTime());

                                       }
                        
                                      m++;
                                    }
                                }
                                // condition all day closed
                                if(!isFoundOtherDay){
                                    storeWithDetails.setStoreTimingMessage("Temporarily closed");

                                }
                                                    
                            }

                            
                        }
                        else{
                            storeWithDetails.setIsOpen(true); 
                            try {
                                if(formatter.parse(currentTime).after(formatter.parse(stt.getOpenTime())) && formatter.parse(currentTime).before(formatter.parse(stt.getCloseTime())) )
                                {
                                    storeWithDetails.setIsOpen(true); 
                                    
                                }else{
                                    //open today but the store timing not yet open 
                                    storeWithDetails.setIsOpen(false); 
                                    storeWithDetails.setStoreTimingMessage("Please come back at "+stt.getOpenTime());
                                }
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                          
                        }
                        return stt;
                    })
                    .findFirst().get();
                }
                //to set store close desciprtion

                //let say it is ecommerce where merchant set 24 hours open then we set it as open
                if (storeWithDetails.getIsAlwaysOpen()){
                    storeWithDetails.setIsOpen(true); 

                }

                if (storeWithDetails.getSnoozeStartTime()!=null && storeWithDetails.getSnoozeEndTime()!=null) {
                    int resultSnooze = storeWithDetails.getSnoozeEndTime().compareTo(Calendar.getInstance().getTime());
                    if (resultSnooze < 0) {
                        storeWithDetails.setIsSnooze(false);

                        st.snoozeStartTime = null;
                        st.snoozeEndTime = null;
                        st.isSnooze = false;
                        st.snoozeReason = null;
                        storeWithDetails.setStoreSnooze(st);


                    } else {
                
                        storeWithDetails.setIsSnooze(true);
                        RegionCountry t = storeWithDetails.getRegionCountry();
                
                        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store timezone:"+t.getTimezone());
                        LocalDateTime startTime = DateTimeUtil.convertToLocalDateTimeViaInstant(storeWithDetails.getSnoozeStartTime(), ZoneId.of(t.getTimezone()));
                        LocalDateTime endTime = DateTimeUtil.convertToLocalDateTimeViaInstant(storeWithDetails.getSnoozeEndTime(), ZoneId.of(t.getTimezone()));
                        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Snooze End Time in store timezone:"+endTime);
                        
                            st.snoozeStartTime = startTime;
                            st.snoozeEndTime = endTime;
                            st.isSnooze = true;
                            st.snoozeReason = storeWithDetails.getSnoozeReason();
                     
                            storeWithDetails.setStoreSnooze(st);
                        
                            
             
                    }
                } else {
                    storeWithDetails.setIsSnooze(false);
                    st.snoozeStartTime = null;
                    st.snoozeEndTime = null;
                    st.isSnooze = false;
                    st.snoozeReason = null;
                    storeWithDetails.setStoreSnooze(st);
                    
                }   
                
                storeWithDetailsList[x]=storeWithDetails;
            }

            //create custom pageable object with modified content
            CustomPageable customPageable = new CustomPageable();
            customPageable.content = storeWithDetailsList;
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
            
            response.setData(customPageable);
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
        
        StoreWithDetails storeWithDetails = optStore.get();       
        List<StoreAssets> storeAssetsList = storeAssetsRepository.findByStoreId(id);
        
        storeAssetsList = StoreAssetsUtility.SetDefaultAsset(storeWithDetails.getVerticalCode(), id, storeAssetsList,
                storeAssetsRepository, regionVerticalRepository, 
                storeBannerFnbDefaultPath, storeBannerEcommerceDefaultPath,
                storeLogoDefaultPath, 
                storeFavIconSymplifiedPath, storeFavIconDeliverinPath, storeFavIconEasydukanPath,assetServiceUrl);        
        storeWithDetails.setStoreAssets(storeAssetsList);
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND id: " + id);
        response.setData(storeWithDetails);
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
        StoreAsset storeAsset = new StoreAsset();
        RegionVertical storeRegionVertical = null;
        
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

            //set isDisplayMap to false
            if (bodyStore.getIsDisplayMap()==null) {
                bodyStore.setIsDisplayMap(false);
            }
            // set isDineIn to false
            if (bodyStore.getIsDineIn()==null) {
                bodyStore.setIsDineIn(false);
            }
            // set default dineInPayementtype
            if (bodyStore.getDineInPaymentType()==null) {
                bodyStore.setDineInPaymentType("COD");
            }

            // set default dineInPayementtype
            if (bodyStore.getDineInOption()==null) {
                bodyStore.setDineInOption("SELFCOLLECT");
            }

            // set isDineIn to false
            if (bodyStore.getDineInConsolidatedOrder()==null) {
                bodyStore.setDineInConsolidatedOrder(false);
            }

            // set isalwaysopen to false
            if (bodyStore.getIsAlwaysOpen()==null) {
                bodyStore.setIsAlwaysOpen(false);
            }

            // set default isdelivery for ecommerce =true, 
              if (bodyStore.getIsDelivery()==null) {

                if(bodyStore.getVerticalCode().contains("Commerce")){

                    bodyStore.setIsDelivery(true);
                    
                } else{

                    bodyStore.setIsDelivery(false);

                }
            }
            
            if (bodyStore.getIsBranch()==false) {
                //only create domain for non-branch store
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "create store domain for non-branch", "");
                
                //customer will enter domain
                String baseDomain = "";
                Optional<RegionVertical> regionVertical = regionVerticalRepository.findById(bodyStore.getVerticalCode());
                if (regionVertical.isPresent()) {
                    baseDomain = regionVertical.get().getDomain();
                    storeRegionVertical = regionVertical.get();
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
                
                //generate qr code
                
                String storeUrl = "https://"+savedStore.getDomain();
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Generating qrcode for "+storeUrl);
                ByteArrayOutputStream baos = QrCodeGenerator.generateQRCodeAsOutputStream(storeUrl);
                
                MultipartFile multipartFile = new MultipartImage(baos.toByteArray(), savedStore.getId() + "-qrcode", "QRCODE", "PNG", 0);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Qrcode Filename: " + multipartFile.getOriginalFilename());
                String bannerStoragePath = fileStorageService.saveStoreAsset(multipartFile, savedStore.getId() + "-qrcode");
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Qrcode storagePath: " + bannerStoragePath);
                String qrCodeUrl = storeAssetsBaseUrl + savedStore.getId() + "-qrcode";
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Qrcode Url: " + qrCodeUrl);
                storeAsset.setStoreId(savedStore.getId());
                storeAsset.setQrCodeUrl(qrCodeUrl);
                storeAssetRepository.save(storeAsset);
                                
                StoreCreationResponse scrCsr = storeLiveChatService.createGroup(domain + "-csr");

                if (scrCsr == null) {
                    Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "csr group could not created", "");
                    //storeRepository.delete(savedStore);
                    Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group could not be created", "");
                    //response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    //response.setError("store group could not be created");
                    //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
                    //storeLiveChatService.deleteGroup(scrOrders.get_id());
                    //storeRepository.delete(savedStore);
                    Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "store group could not be created", "");
                    //response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    //response.setError("store group could not be created");
                    //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
            
            if (storeRegionVertical.getCommissionPercentage()!=null) {
                rate = storeRegionVertical.getCommissionPercentage();
            }
            if (storeRegionVertical.getMinChargeAmount()!=null) {
                minChargeAmount = storeRegionVertical.getMinChargeAmount();
            }
            StoreCommission sc = new StoreCommission();
            sc.setRate(rate);
            sc.setMinChargeAmount(minChargeAmount);
            sc.setStoreId(savedStore.getId());
            sc.setSettlementDays(5);
            storeComisssionRepository.save(sc);
            response.setData(savedStore);
            
            //set default store asset            
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
            
            //create center code for Pakistan Store
            if (bodyStore.getRegionCountryId().equals("PAK")) {
                deliveryService.createCenterCode(savedStore.getId());
            }

            
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
//            whatsappService.sendWhatsappMessage(recipients, client.getUsername(), password, "NEWSTORE");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " error creating store ", "", e);

            if(e.getMessage().contains("Whatsapp API return error")){
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);   

            }else{
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);   
            }
          
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
            if (bodyStore.getStoreDescription()!=null) { 
                if (bodyStore.getStoreDescription().length()>storeDescriptionLength) {
                    String shortDescription = bodyStore.getStoreDescription().substring(0, storeDescriptionLength);
                    bodyStore.setStoreDescription(shortDescription);
                }
            }
            
            store.update(bodyStore);
            store = storeRepository.save(store);
            
            //check if merchant change domain            
            if (bodyStore.getDomain() != null) {
                //regenerate qr code
                String storeUrl = "https://"+store.getDomain();
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Generating qrcode for "+storeUrl);
                ByteArrayOutputStream baos = QrCodeGenerator.generateQRCodeAsOutputStream(storeUrl);
                
                MultipartFile multipartFile = new MultipartImage(baos.toByteArray(), store.getId() + "-qrcode", "QRCODE", "PNG", 0);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Qrcode Filename: " + multipartFile.getOriginalFilename());
                String bannerStoragePath = fileStorageService.saveStoreAsset(multipartFile, store.getId() + "-qrcode");
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Qrcode storagePath: " + bannerStoragePath);
                String qrCodeUrl = storeAssetsBaseUrl + store.getId() + "-qrcode";
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Qrcode Url: " + qrCodeUrl);
                
                
                Optional<StoreAsset> storeAssetOpt = storeAssetRepository.findById(store.getId());
                StoreAsset storeAsset = null;
                if (storeAssetOpt.isPresent()) {
                    storeAsset = storeAssetOpt.get();
                } else {
                    storeAsset = new StoreAsset();
                    storeAsset.setStoreId(store.getId());
                }               
                storeAsset.setQrCodeUrl(qrCodeUrl);
                storeAssetRepository.save(storeAsset);
            }
            
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "updated store with id: " + id);
            response.setData(store);
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
        storeAssetRepository.deleteByStoreId(id);
        storeAssetsRepository.deleteByStoreId(id);
        storeDeliveryPeriodsRepository.deleteByStoreId(id);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store Assets deleted for storeId:"+id);
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
    public ResponseEntity<HttpResponse> getStoreCategoryByStoreId(HttpServletRequest request,
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

        //split string to get subdomain
        Integer i = domain.indexOf('.');
        String subdomain;

        if(i == -1){
            subdomain = domain;
        }else{
            subdomain = domain.substring(0,i);
        }

        Optional<ReserveDomain> optReserveDomain = reserveDomainRepository.getReserveDomain(subdomain);

        if (!optStore.isPresent() && !optReserveDomain.isPresent()) {
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

    @GetMapping(path = {"/checkprefix"}, name = "stores-check-name-availability", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-check-name-availability', 'all')")
    public ResponseEntity<HttpResponse> checkPrefixAvailability(HttpServletRequest request,
            @RequestParam(required = true) String storePrefix
    ) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " id: " + storePrefix, "");

        if(storePrefix.length() != 4){
            response.setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<StoreWithDetails> optStore = storeWithDetailsRepository.findByStorePrefix(storePrefix);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Name: " + storePrefix+" IS available");
            response.setStatus(HttpStatus.OK);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Name: " + storePrefix+" NOT available");
            response.setStatus(HttpStatus.CONFLICT);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
       
        
    }
    
    @PostMapping(path = {"/generateprefix"}, name = "stores-check-name-availability", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-check-name-availability', 'all')")
    public ResponseEntity<HttpResponse> generatePrefix(HttpServletRequest request,
            @Valid @RequestBody Store store) throws Exception {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " storename: " + store.getName(), "");
        
        String storePrefix = getNameAbreviation(store.getName());
        
        boolean isDuplicate=true;
        int sequence=1;
        while (isDuplicate) {
            Optional<StoreWithDetails> optStore = storeWithDetailsRepository.findByStorePrefix(storePrefix);
            if (!optStore.isPresent()) {
                isDuplicate=false;
            } else {
                String sequenceStr = String.valueOf(sequence);
                if (sequence<10)
                    sequenceStr="0" + sequence;
                storePrefix=storePrefix+sequenceStr;
                sequence++;
            }
        }
        
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " Store Prefix generated:" + storePrefix);
        response.setData(storePrefix);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }
    
    public Specification<StoreWithDetails> getStoreSpec(
            String[] verticalCodeList, String domain, Example<StoreWithDetails> example) {

        return (Specification<StoreWithDetails>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (verticalCodeList!=null) {
                int typeCount = verticalCodeList.length;
                List<Predicate> typePredicatesList = new ArrayList<>();
                for (int i=0;i<verticalCodeList.length;i++) {
                    Predicate predicateForCompletionStatus = builder.equal(root.get("verticalCode"), verticalCodeList[i]);
                    typePredicatesList.add(predicateForCompletionStatus);
                }

                Predicate finalPredicate = builder.or(typePredicatesList.toArray(new Predicate[typeCount]));
                predicates.add(finalPredicate);
            }
            
            /*
            //ENABLE THIS TO search by exact full domain
            if (domain != null) {
                predicates.add(builder.equal(root.get("domain"), domain));
            }*/
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
    
    @GetMapping(path = {"/top"}, name = "stores-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('stores-get', 'all')")
    public ResponseEntity<HttpResponse> getTopStore(HttpServletRequest request,
            @RequestParam(required = false) String countryId) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "", "");

        try {            
            
            StoreWithDetails store = new StoreWithDetails();
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "countryId: " + countryId, "");
            
            int totalStore = storeRepository.getTotalStore(countryId);           
            int page=0;
            int pageSize=5;
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "page:0 pageSize:5" , "");
            Pageable pageable = PageRequest.of(page, pageSize);
           
            Page<StoreAssets> fetchedPage = storeAssetsRepository.findByCountry(countryId, pageable);
            List<StoreAssets> storeAssetList = fetchedPage.getContent();

            for(StoreAssets sa : storeAssetList){

                //handle null
                if(sa.getAssetUrl() != null){
                    sa.setAssetUrl(assetServiceUrl+sa.getAssetUrl());

                } else{
                    sa.setAssetUrl(null);

                }
            }
            
            TopStore topStore = new TopStore();
            topStore.setTotalStore(totalStore);
            topStore.setTopStoreAsset(storeAssetList);
            response.setData(topStore);
            response.setStatus(HttpStatus.OK);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Error fetching stores", "", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setError(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
    
    
    public String getNameAbreviation(String storeName) {
        String abbreviation = "";

        if (storeName.length() <= 2) {
            abbreviation = storeName;
        } else {
            String[] myName = storeName.split(" ");
            
            if (myName.length<2 && storeName.length()>=2) {
                abbreviation = storeName.substring(0, 2);
            } else {            
                for (int i = 0; i < myName.length; i++) {
                    String s = myName[i];
                    abbreviation = abbreviation + s.charAt(0);

                    if (abbreviation.length() == 2) {
                        break;
                    }
                }
            }
        }
        return abbreviation;
    }
     
    /*
    //not used anymore, will be removed
    @GetMapping(path = {"/qrcode/{storeId}"}, name = "stores-get", produces = "image/png")
    @PreAuthorize("hasAnyAuthority('stores-get-by-id', 'all')")
    public ResponseEntity<BufferedImage> generateQrCode(HttpServletRequest request,
            @PathVariable("storeId") String storeId)
    throws Exception {
        String logprefix = request.getRequestURI();
        Optional<Store> optStore = storeRepository.findById(storeId);
        if (optStore.isPresent()) {           
            String url = "https://"+optStore.get().getDomain();
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Generating qrcode for "+url);
            BufferedImage image = QrCodeGenerator.generateQRCodeImage(url);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Image generated:"+image.toString());
            return new ResponseEntity<>(image, HttpStatus.OK);
        } else {
            return null;
        }
    }*/
    
}
