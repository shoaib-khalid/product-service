package com.kalsym.product.service.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.MarketplaceBannerConfig;
import com.kalsym.product.service.model.MarketplacePopUp;


@Repository
public interface MarketplacePopUpRepository extends JpaRepository<MarketplacePopUp,Integer>,JpaSpecificationExecutor<MarketplacePopUp> {
    
   
}

