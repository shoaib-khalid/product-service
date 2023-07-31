package com.kalsym.product.service.model.product;

import com.kalsym.product.service.model.store.Voucher;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ProductVoucherResponse implements Serializable {

    private Voucher voucher;
    private Product product;
}
