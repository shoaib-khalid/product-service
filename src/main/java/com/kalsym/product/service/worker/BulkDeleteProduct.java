package com.kalsym.product.service.worker;

import java.util.List;
import java.util.Optional;

import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.service.CloneProductService;

public class BulkDeleteProduct extends Thread {
    
    private List<String> productIds;
    private CloneProductService cloneProductService;

    public BulkDeleteProduct(List<String> productIds,CloneProductService cloneProductService){

        this.productIds = productIds;
        this.cloneProductService = cloneProductService;

    }

    public void run(){
        super.run();
        cloneProductService.bulkDeleteProducts(productIds);
    }
}
