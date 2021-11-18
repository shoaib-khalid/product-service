package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
    
    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private LocalDateTime updated;

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

}
