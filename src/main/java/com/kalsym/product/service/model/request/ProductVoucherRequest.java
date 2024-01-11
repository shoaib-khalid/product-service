package com.kalsym.product.service.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalsym.product.service.enums.*;
import com.kalsym.product.service.model.store.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@ToString
@Getter
@Setter
public class ProductVoucherRequest {

    private String name;
    private Double voucherValue;
    private Double sellingPrice;
    private String voucherCode;
    private Integer totalQuantity;
    private String currencyLabel;
    private String verticalCode;
    private String description;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    private Boolean isGlobalStore;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    private List<ProductVoucherTermsRequest> voucherTerms;

}
