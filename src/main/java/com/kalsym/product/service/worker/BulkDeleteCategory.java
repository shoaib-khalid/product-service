package com.kalsym.product.service.worker;

import java.util.List;
import com.kalsym.product.service.service.CloneProductService;

public class BulkDeleteCategory extends Thread {
    
    private List<String> categoryIds;
    private CloneProductService cloneProductService;

    public BulkDeleteCategory(List<String> categoryIds,CloneProductService cloneProductService){

        this.categoryIds = categoryIds;
        this.cloneProductService = cloneProductService;

    }

    public void run(){
        super.run();
        cloneProductService.bulkDeleteCategory(categoryIds);
    }
}
