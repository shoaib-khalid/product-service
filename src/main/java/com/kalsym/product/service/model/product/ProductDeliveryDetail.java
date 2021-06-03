package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Entity
@Table(name = "product_delivery_detail")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ProductDeliveryDetail implements Serializable {

    @Id
    private String productId;

    private String type;
    private String itemType;

 
    public void update(ProductDeliveryDetail productDeliveryDetail) {
        if (productDeliveryDetail.getType() != null) {
            type = productDeliveryDetail.getType();
        }

       

        if (productDeliveryDetail.getItemType() != null) {
            itemType = productDeliveryDetail.getItemType();
        }
    }

}
