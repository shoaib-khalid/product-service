package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.RegionCountryStateCity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionCountryStateCityRepository extends JpaRepository<RegionCountryStateCity, String> {
    // RegionCountryStateCityRepository findByCityContains(String city);
}