package com.kalsym.product.service.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.MarketplaceBannerConfig;


@Repository
public interface MarketplaceBannerConfigRepository extends JpaRepository<MarketplaceBannerConfig,Integer> {
    
    @Query(
        value =
        " SELECT * "
        +"FROM marketplace_banner_config mbc "
        +"WHERE regionCountryId = :regionCountryId "
        +"LIMIT 10", 
        nativeQuery = true
    )
    Collection<MarketplaceBannerConfig> getBannerByCountryId(
        @Param("regionCountryId") String regionCountryId
    );
   
}

