package com.kalsym.product.service.model.request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ProductAddonGroupRequest {
    
    private String id;

    private String addonTemplateGroupId;

    private Integer minAllowed;

    private Integer maxAllowed;

    private Integer sequenceNumber;

    private String productId;

    private String status;

}
