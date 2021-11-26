package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = "product_inventory_item")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@IdClass(ProductInventoryItemId.class)
public class ProductInventoryItem implements Serializable {

    @Id
    private String itemCode;

    @Id
    private String productVariantAvailableId;

    private String productId;

    private Integer sequenceNumber;
    
    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "productVariantAvailableId", referencedColumnName = "id", insertable = false, updatable = false, nullable = true)    
    private ProductVariantAvailable productVariantAvailable;
    
    
     public void update(ProductInventoryItem pi) {
        
        if (null != pi.getProductId()) {
            productId = pi.getProductId();
        }
        
        if (null != pi.getSequenceNumber()) {
            sequenceNumber = pi.getSequenceNumber();
        }

    }
}
