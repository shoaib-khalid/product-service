package com.kalsym.product.service.model.request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ProductAddonGroupRequest {
    
    private String id;

    private String addonGroupId;

    private Integer minAllowed;

    private Integer maxAllowed;

    private Integer sequenceNumber;

    private Boolean isDefault;

    private String productId;
}
