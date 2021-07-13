package com.kalsym.product.service.model.store;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.product.service.enums.DeliveryType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "store_delivery_detail")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class StoreDeliveryDetail implements Serializable {

    @Id
    private String storeId;

//    @Enumerated(EnumType.STRING)
    private String type;
    private String itemType;

  

    private Integer maxOrderQuantityForBike;

    public void update(StoreDeliveryDetail storeDeliveryDetail) {
        if (storeDeliveryDetail.getType() != null) {
            type = DeliveryType.fromString(storeDeliveryDetail.getType()).toString();
        }

       
        if (storeDeliveryDetail.getItemType() != null) {
            itemType = storeDeliveryDetail.getItemType();
        }

        if (storeDeliveryDetail.getMaxOrderQuantityForBike() != null) {
            maxOrderQuantityForBike = storeDeliveryDetail.getMaxOrderQuantityForBike();
        }

    }

}
