package com.kalsym.product.service.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.MarketplaceBannerConfig;
import com.kalsym.product.service.repository.MarketplaceBannerConfigRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MarketPlaceBannerConfigService {

    @Autowired
    MarketplaceBannerConfigRepository marketplaceBannerConfigRepository;
    
    public List<MarketplaceBannerConfig> getQueryRegionCountryId(String regionCountryId, String type){

        if(type == null){
            type = "";
        }
     
        Collection<MarketplaceBannerConfig> result = marketplaceBannerConfigRepository.getBannerByCountryId(regionCountryId,type);

        List<MarketplaceBannerConfig> output = new ArrayList<MarketplaceBannerConfig>(result);

        return output;

    }
    
     
}