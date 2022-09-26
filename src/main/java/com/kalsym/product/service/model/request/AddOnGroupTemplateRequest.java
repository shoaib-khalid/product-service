package com.kalsym.product.service.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class AddOnGroupTemplateRequest {

    private String id;
	private String storeId;
    private String title;

}
