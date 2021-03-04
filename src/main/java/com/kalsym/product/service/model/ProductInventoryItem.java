package com.kalsym.product.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.EmbeddedId;
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
@Table(name = "product_inventory_item")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//Using IdClass annotation to resolve the composite PK problem in hibernate
@IdClass(ProductInventoryItemId.class)
public class ProductInventoryItem implements Serializable {

//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "itemCode", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    private ProductInventory productInventory;
    
//     @EmbeddedId
//    private EProductInventoryItemId productInventoryItemId;
    @Id
    private String itemCode;

//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "variantAvailableId", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    private ProductVariantAvailable productVariantAvailable;
    @Id
    private String variantAvailableId;

}
