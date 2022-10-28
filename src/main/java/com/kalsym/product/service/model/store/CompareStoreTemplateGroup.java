package com.kalsym.product.service.model.store;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CompareStoreTemplateGroup {
 
    private String storeTemplateGroupId;
    private String title;
    private String branchTemplateGroupId;

    private List<CompareStoreTemplateItem> compareTemplateItem;
}
