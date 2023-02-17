package com.kalsym.product.service.model.product;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.VehicleType;
import com.kalsym.product.service.model.store.StoreCategoryMain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

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
public class ProductWithDetails implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String name;

    private String description;

    private String storeId;

    private Integer shortId;

    @Column(name = "categoryId")
    private String categoryId;

    @OneToOne()
    @JoinColumn(name = "categoryId",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    private StoreCategoryMain storeCategory;  

    private String status;

    private String thumbnailUrl;

    private String vendor;

    private String region;

    private String seoUrl;

    private String seoName;

    @Transient 
    String seoUrlMarketPlace;

    @Transient 
    String seoUrlSf;

    @Transient 
    String seoNameMarketplace;

    private Boolean trackQuantity;

    private Boolean allowOutOfStockPurchases;

    private Integer minQuantityForAlarm;
    
    private String packingSize;
    
    private Boolean isPackage;
    
    private Boolean isNoteOptional;
    
    private Boolean hasAddOn;

    private Boolean isCustomPrice;

    private String customNote;

    private Integer sequenceNumber;
     
    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private LocalDateTime updated;
    
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private List<ProductVariant> productVariants;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private List<ProductInventoryWithDetails> productInventories;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Set<ProductReview> productReviews = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private List<ProductAsset> productAssets;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "productId", insertable = false, updatable = false, nullable = true)
    private ProductDeliveryDetail productDeliveryDetail;

    public String getSeoUrlMarketPlace() {

        return ProductServiceApplication.MARKETPLACEURL+"/"+shortId+"-"+seoName;
    }

    public String getSeoUrlSf() {

        return seoUrl+"/"+shortId;
    }

    public String getSeoNameMarketplace() {

        return shortId+"-"+seoName;
    }

    public void update(ProductWithDetails product) {
        if (null != product.getName()) {
            name = product.getName();
        }

        if (null != product.getCategoryId()) {
            categoryId = product.getCategoryId();
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
        final ProductWithDetails other = (ProductWithDetails) obj;
        return Objects.equals(this.id, other.getId());
    }

}
