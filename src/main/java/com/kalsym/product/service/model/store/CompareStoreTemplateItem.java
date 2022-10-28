package com.kalsym.product.service.model.store;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CompareStoreTemplateItem {
   
    private String storeTemplateItem;
    private String name;
    private String branchTemplateItem;
}
