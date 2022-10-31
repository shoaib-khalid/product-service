package com.kalsym.product.service.worker;


import java.util.List;
import java.util.Optional;

import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.service.CloneProductService;

public class CloneSelectedProduct extends Thread {
    
    private String storeId;
    private String storeBranchId;
    private Optional<Store> optStoreBranch;
    private List<String> productIds;
    private CloneProductService cloneProductService;

    public CloneSelectedProduct(String storeId, String storeBranchId, Optional<Store> optStoreBranch, List<String> productIds,CloneProductService cloneProductService){
        this.storeId = storeId;
        this.storeBranchId = storeBranchId;
        this.optStoreBranch = optStoreBranch;
        this.productIds = productIds;
        this.cloneProductService = cloneProductService;
    }

    public void run(){
        super.run();
        cloneProductService.cloneProductById(storeId,storeBranchId,optStoreBranch,productIds);
    }
}
