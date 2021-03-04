package com.kalsym.product.service.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
public class EProductInventoryItemId implements Serializable {

    @Column(name = "itemCode")
    private String itemCode;

    @Column(name = "variantAvailableId")
    private String variantAvailableId;

    public EProductInventoryItemId() {
    }

    public EProductInventoryItemId(String itemCode, String variantAvailableId) {
        this.itemCode = itemCode;
        this.variantAvailableId = variantAvailableId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EProductInventoryItemId that = (EProductInventoryItemId) o;
        return itemCode.equals(that.itemCode)
                && variantAvailableId.equals(that.variantAvailableId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemCode, variantAvailableId);
    }
}
