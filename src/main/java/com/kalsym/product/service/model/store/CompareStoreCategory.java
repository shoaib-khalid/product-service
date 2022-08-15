package com.kalsym.product.service.model.store;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
//for comparing purpose
public class CompareStoreCategory {
    

    private String storeOwnerCategoryId;
    private String name;
    private String branchCategoryId;
}
