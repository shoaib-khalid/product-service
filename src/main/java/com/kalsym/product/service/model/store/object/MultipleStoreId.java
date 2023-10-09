package com.kalsym.product.service.model.store.object;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class MultipleStoreId implements Serializable {
    private String storeId;

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
