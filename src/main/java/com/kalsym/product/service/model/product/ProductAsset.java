package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.*;

import com.kalsym.product.service.ProductServiceApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "product_asset")
@NoArgsConstructor
public class ProductAsset implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String itemCode;

    private String name;
    private String url;
    private String productId;

    private Boolean isThumbnail;

    @Transient
    String imageUrl;

    public String getImageUrl() {

        if (url != null) {
            if (url.startsWith(ProductServiceApplication.ASSETURL)) {
                // 'url' already has 'ProductServiceApplication.ASSETURL' appended
                return url;
            } else {
                // Append 'ProductServiceApplication.ASSETURL' to 'url'
                return ProductServiceApplication.ASSETURL + url;
            }
        } else {
            return null;
        }
    }


    public void update(ProductAsset product) {
        if (null != product.getName()) {
            name = product.getName();
        }

        if (null != product.getUrl()) {
            url = product.getUrl();
        }

        if (null != product.getIsThumbnail()) {
            isThumbnail = product.getIsThumbnail();
        }

    }

}
