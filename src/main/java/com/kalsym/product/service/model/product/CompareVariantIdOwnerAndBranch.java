package com.kalsym.product.service.model.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
//for comparing purpose

public class CompareVariantIdOwnerAndBranch {
    
    private String ownerVariantId;
    private String branchVariantId;

}
