package com.kalsym.product.service.model.product;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "product_feature")
@NoArgsConstructor
public class ProductFeature implements Serializable {

    @Id
    private String id;

    private String name;
    private String imageUrl;

    private String productId;

}
