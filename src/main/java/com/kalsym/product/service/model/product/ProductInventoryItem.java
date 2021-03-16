package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
//Using IdClass annotation to resolve the composite PK problem in hibernate
@IdClass(ProductInventoryItemId.class)
public class ProductInventoryItem implements Serializable {

    @Id
    private String itemCode;

    @Id
    private String productVariantAvailableId;

    private String productId;

    //    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "itemCode", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    private ProductInventory productInventory;
//     @EmbeddedId
//    private EProductInventoryItemId productInventoryItemId;
    //    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "variantAvailableId", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    private ProductVariantAvailable productVariantAvailable;
}
