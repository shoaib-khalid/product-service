package com.kalsym.product.service.model.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalsym.product.service.model.RegionCountry;
import com.kalsym.product.service.model.RegionVertical;
import com.kalsym.product.service.model.StoreSnooze;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 7cu
 */
@Entity
@Getter
@Setter
@Table(name = "store")
@ToString
@NoArgsConstructor
public class StoreWithDetails implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String name;

    private String city;

    private String address;

    private String clientId;

    private String verticalCode;

    private String storeDescription;

    private String postcode;

    private String email;

    private String domain;

    private String liveChatOrdersGroupId;

    private String liveChatOrdersGroupName;

    private String liveChatCsrGroupId;

    private String liveChatCsrGroupName;

    private String regionCountryId;

    private String phoneNumber;

    private String regionCountryStateId;

    private String paymentType;

    private Integer serviceChargesPercentage;
    
    private String googleAnalyticId;
    
    private String displayAddress;

    private Boolean isDisplayMap;

    private Boolean isDineIn;

    private String dineInOption;

    private String dineInPaymentType;

    private Boolean dineInConsolidatedOrder;

    private Boolean isAlwaysOpen;

    private Boolean isDelivery;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date snoozeStartTime;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date snoozeEndTime;
    
    private String snoozeReason;

    @Transient
    Boolean isSnooze;

    @Transient
    StoreSnooze storeSnooze;
        
    private Boolean isBranch;
            
    private String latitude;
    
    private String longitude;

    private String storePrefix;
    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updated;
    
    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "regionCountryId", referencedColumnName = "id", insertable = false, updatable = false, nullable = true)
    private RegionCountry regionCountry;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "storeId", insertable = false, updatable = false, nullable = true)
    private List<StoreTiming> storeTiming;
    
    /*@OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "id", referencedColumnName = "storeId", insertable = false, updatable = false, nullable = true)
    private StoreAsset storeAsset;
    */
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", insertable = false, updatable = false, nullable = true)
    private List<StoreAssets> storeAssets;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verticalCode", insertable = false, updatable = false, nullable = true)
    private RegionVertical regionVertical;
    
    @Transient 
    private Integer completionPercentage;   

    @Transient 
    private Boolean isOpen;
    
    @Transient 
    private String storeTimingMessage;
    
    public void update(StoreWithDetails store) {

        if (null != store.getCity()) {
            city = store.getCity();
        }

        if (null != store.getName()) {
            name = store.getName();
        }

        if (null != store.getAddress()) {
            address = store.getAddress();
        }

        if (null != store.getClientId()) {
            clientId = store.getClientId();
        }
        if (null != store.getVerticalCode()) {
            verticalCode = store.getVerticalCode();
        }

        if (null != store.getStoreDescription()) {
            storeDescription = store.getStoreDescription();
        }

        if (null != store.getPostcode()) {
            postcode = store.getPostcode();
        }

        if (null != store.getRegionCountryId()) {
            regionCountryId = store.getRegionCountryId();
        }

        if (null != store.getPhoneNumber()) {
            phoneNumber = store.getPhoneNumber();
        }

        if (null != store.getRegionCountryStateId()) {
            regionCountryStateId = store.getRegionCountryStateId();
        }

        if (null != store.getServiceChargesPercentage()) {
            serviceChargesPercentage = store.getServiceChargesPercentage();
        }

        if (null != store.getEmail()) {
            email = store.getEmail();
        }

        if (null != store.getPaymentType()) {
            paymentType = store.getPaymentType();
        }
         
        if (null != store.getIsBranch()) {
            isBranch = store.getIsBranch();
        }
        
        if (null != store.getLatitude()) {
            latitude = store.getLatitude();
        }
        
        if (null != store.getLongitude()) {
            longitude = store.getLongitude();
        }
        
        if (null != store.getDisplayAddress()) {
            displayAddress = store.getDisplayAddress();
        }

        if (null != store.getIsDisplayMap()) {
            isDisplayMap = store.getIsDisplayMap();
        }

        if (null != store.getIsDineIn()) {
            isDineIn = store.getIsDineIn();
        }

        if (null != store.getDineInOption()) {
            dineInOption = store.getDineInOption();
        }

        if (null != store.getDineInOption()) {
            dineInPaymentType = store.getDineInPaymentType();
        }

        if (null != store.getIsDelivery()) {
            isDelivery = store.getIsDelivery();
        }

        if (null != store.getDineInConsolidatedOrder()) {
            dineInConsolidatedOrder = store.getDineInConsolidatedOrder();
        }

        if (null != store.getIsAlwaysOpen()) {
            isAlwaysOpen = store.getIsAlwaysOpen();
        }

        if (null != store.getStorePrefix()) {
            storePrefix = store.getStorePrefix();
        }

    }
    
}
