package com.kalsym.product.service.worker;

import java.util.List;

import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.service.CloneProductService;

public class BulkEditSequenceProduct extends Thread {
    
    private List<Product> products;
    private CloneProductService cloneProductService;

    public BulkEditSequenceProduct(List<Product> products,CloneProductService cloneProductService){

        this.products = products;
        this.cloneProductService = cloneProductService;

    }

    public void run(){
        super.run();
        cloneProductService.bulkEditProductSequence(products);
    }
}
