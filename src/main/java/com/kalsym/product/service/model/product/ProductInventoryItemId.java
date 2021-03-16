package com.kalsym.product.service.model.product;

import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
public class ProductInventoryItemId implements Serializable {

    private String itemCode;
    private String productVariantAvailableId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductInventoryItemId productInventoryItemId = (ProductInventoryItemId) o;
        return itemCode.equals(productInventoryItemId.itemCode)
                && productVariantAvailableId.equals(productVariantAvailableId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemCode, productVariantAvailableId);
    }
}
