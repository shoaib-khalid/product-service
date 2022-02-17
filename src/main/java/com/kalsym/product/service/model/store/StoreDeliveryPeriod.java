package com.kalsym.product.service.model.store;

import com.kalsym.product.service.model.product.*;
import com.kalsym.product.service.enums.DeliveryPeriod;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@Table(name = "store_delivery_period")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class StoreDeliveryPeriod implements Serializable {

    @Id
    private String id;
    
    @Enumerated(EnumType.STRING)
    private DeliveryPeriod deliveryPeriod;
    
    private String storeId;
    
    private Boolean enabled;

}
