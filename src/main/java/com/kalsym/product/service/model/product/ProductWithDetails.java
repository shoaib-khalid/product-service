package com.kalsym.product.service.model.product;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

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

<<<<<<< HEAD
//    private Integer stock;
=======
>>>>>>> origin/master

    private String description;

    private String storeId;

    @Column(name = "categoryId")
    private String categoryId;

    private String status;

    private String thumbnailUrl;

    private String vendor;

<<<<<<< HEAD
//    private String barcode;
=======
>>>>>>> origin/master

    private String region;

    private String seoUrl;
    
    private String seoName;
    
<<<<<<< HEAD
//    private Double weight;
    
    private Boolean trackQuantity;
    private Boolean allowOutOfStockPurchases;
    private int minQuantityForAlarm;
=======
>>>>>>> origin/master

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

    public void update(ProductWithDetails product) {
        if (null != product.getName()) {
            name = product.getName();
        }

<<<<<<< HEAD
//        if (null != product.getStock()) {
//            stock = product.getStock();
//        }
=======

>>>>>>> origin/master

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
