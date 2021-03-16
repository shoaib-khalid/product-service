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
public class ProductReviewId implements Serializable {

    private String productId;
    private String customerId;

    public ProductReviewId(String productId, String customerId) {
        this.productId = productId;
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductReviewId productReviewId = (ProductReviewId) o;
        return productId.equals(productReviewId.productId)
                && customerId.equals(productReviewId.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, customerId);
    }
}
