package com.kalsym.product.service.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class AddOnTemplateItemRequest {
   
    private String id;

    private String groupId;

    private String name;

    private Double price;

    private Double dineInPrice;
}
