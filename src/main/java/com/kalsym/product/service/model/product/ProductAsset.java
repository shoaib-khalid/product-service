package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
