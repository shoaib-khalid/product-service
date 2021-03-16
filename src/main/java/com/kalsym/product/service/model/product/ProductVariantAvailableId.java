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
public class ProductVariantAvailableId implements Serializable {

    private String id;

    private String variantId;

    private String variantValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductVariantAvailableId productVariantAvailableId = (ProductVariantAvailableId) o;
        return id.equals(productVariantAvailableId.id) && variantId.equals(productVariantAvailableId.variantId)
                && variantValue.equals(productVariantAvailableId.variantValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, variantId, variantValue);
    }
}
