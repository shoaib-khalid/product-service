package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;
import java.util.Objects;

import com.kalsym.product.service.model.store.Voucher;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import com.kalsym.product.service.enums.VehicleType;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "product")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String name;

    private String description;

    private String storeId;

    @Column(name = "categoryId")
    private String categoryId;

    private String status;

    private String thumbnailUrl;

    private String vendor;

    private String region;

    private String seoUrl;

    private String seoName;

    private Boolean trackQuantity;

    private Boolean allowOutOfStockPurchases;

    private Integer minQuantityForAlarm;
    
    private String packingSize;
    
    private Boolean isPackage;
    
    private Boolean isNoteOptional;
    
    private String customNote;

    private Boolean hasAddOn;

    private Boolean isCustomPrice;

    private Integer sequenceNumber;

    private String voucherId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false)
    private Voucher voucher;
    
    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private LocalDateTime updated;
    
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
     
    public void update(Product product) {
        if (null != product.getName()) {
            name = product.getName();
        }        

        if (null != product.getCategoryId()) {
            categoryId = product.getCategoryId();
        }

        if (null != product.getDescription()) {
            description = product.getDescription();
        }

        if (null != product.getStatus()) {
            status = product.getStatus();
        }

        if (null != product.getThumbnailUrl()) {
            thumbnailUrl = product.getThumbnailUrl();
        }

        if (null != product.getVendor()) {
            vendor = product.getVendor();
        }
        
        if (null != product.getRegion()) {
            region = product.getRegion();
        }
        
        if (null != product.getSeoUrl()) {
            seoUrl = product.getSeoUrl();
        }
         
        if (null != product.getSeoName()) {
            seoName = product.getSeoName();
        }
        
        if (null != product.getTrackQuantity()) {
            trackQuantity = product.getTrackQuantity();
        }
        if (null != product.getAllowOutOfStockPurchases()) {
            allowOutOfStockPurchases = product.getAllowOutOfStockPurchases();
        }
        if (null != product.getMinQuantityForAlarm()) {
            minQuantityForAlarm = product.getMinQuantityForAlarm();
        }
        
        if (null != product.getPackingSize()) {
            packingSize = product.getPackingSize();
        }
        
        if (null != product.getIsPackage()) {
            isPackage = product.getIsPackage();
        }
        
        if (null != product.getVehicleType()) {
            vehicleType = product.getVehicleType();
        }
        
        if (null != product.getIsNoteOptional()) {
            isNoteOptional = product.getIsNoteOptional();            
        }

        if (null != product.getHasAddOn()) {
            hasAddOn = product.getHasAddOn();            
        }

        if (null != product.getIsCustomPrice()) {
            isCustomPrice = product.getIsCustomPrice();            
        }

        if (null != product.getSequenceNumber()) {
            sequenceNumber = product.getSequenceNumber();            
        }

        if( null != product.getVoucherId()) {
            voucherId = product.getVoucherId();
        }
        
        customNote = product.getCustomNote();
        
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Product other = (Product) obj;
        return Objects.equals(this.id, other.getId());
    }

    public void setCustomPrice(boolean isCustomPrice) {
        this.isCustomPrice = isCustomPrice;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public static String generateSku(String productName) {
        if (productName == null) {
            return null;
        }

        // Remove leading and trailing white spaces
        productName = productName.trim();

        // Convert the entire string to lowercase
        productName = productName.toLowerCase();

        // Replace spaces with dashes ("-")
        productName = productName.replace(" ", "-");

        // Replace multiple consecutive dashes with a single dash
        productName = productName.replaceAll("-+", "-");

        // Remove all characters that are not alphanumeric or dashes
        productName = productName.replaceAll("[^\\w-]", "");

        return productName;
    }
}
