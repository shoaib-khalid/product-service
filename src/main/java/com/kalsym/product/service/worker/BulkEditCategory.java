package com.kalsym.product.service.worker;

import java.util.List;

import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.service.CloneProductService;

public class BulkEditCategory extends Thread {
    
    private List<StoreCategory> storeCategories;
    private CloneProductService cloneProductService;

    public BulkEditCategory(List<StoreCategory> storeCategories,CloneProductService cloneProductService){

        this.storeCategories = storeCategories;
        this.cloneProductService = cloneProductService;

    }

    public void run(){
        super.run();
        cloneProductService.bulkEditCategory(storeCategories);
    }
}
