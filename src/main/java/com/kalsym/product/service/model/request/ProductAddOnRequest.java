package com.kalsym.product.service.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ProductAddOnRequest {
    private String id;

    private String productId;

    private String addonTemplateItemId;

    private String status;

    private Double price;

    private Double dineInPrice;

    private Integer sequenceNumber;
    
}
