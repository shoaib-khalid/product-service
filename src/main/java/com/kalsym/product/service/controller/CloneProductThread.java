package com.kalsym.product.service.controller;

import java.util.Optional;

import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.service.CloneProductService;

public class CloneProductThread extends Thread {
    
    private String storeId;
    private String storeOwnerId;
    private CloneProductService cloneProductService;
    private Optional<Store> optStore;

    public CloneProductThread(String storeId, String storeOwnerId, Optional<Store> optStore,CloneProductService cloneProductService){

        this.storeId = storeId;
        this.storeOwnerId = storeOwnerId;
        this.cloneProductService = cloneProductService;
        this.optStore = optStore;

    }

    public void run(){
        super.run();
        cloneProductService.cloneProducts(storeId,storeOwnerId,optStore);
    }
}
