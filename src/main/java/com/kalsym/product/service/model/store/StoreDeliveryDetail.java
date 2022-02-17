package com.kalsym.product.service.model.store;

import com.kalsym.product.service.model.product.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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

    private String type;
    private String itemType;
    private Integer maxOrderQuantityForBike;
    private Boolean allowsStorePickup;
    
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "storeId", insertable = false, updatable = false, nullable = true)    
    private List<StoreDeliveryPeriod> storeDeliveryPeriodList;
            
    public void update(StoreDeliveryDetail storeDeliveryDetail) {
        if (storeDeliveryDetail.getType() != null) {
            type = storeDeliveryDetail.getType();
        }

       
        if (storeDeliveryDetail.getItemType() != null) {
            itemType = storeDeliveryDetail.getItemType();
        }
        
        if (storeDeliveryDetail.getAllowsStorePickup()!= null) {
            allowsStorePickup = storeDeliveryDetail.getAllowsStorePickup();
        }

        if (storeDeliveryDetail.getMaxOrderQuantityForBike() != null) {
            maxOrderQuantityForBike = storeDeliveryDetail.getMaxOrderQuantityForBike();
        }

    }

}
