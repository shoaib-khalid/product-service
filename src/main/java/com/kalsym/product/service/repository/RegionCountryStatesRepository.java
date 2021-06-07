package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.RegionCountryState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface RegionCountryStatesRepository extends PagingAndSortingRepository<RegionCountryState, String>, JpaRepository<RegionCountryState, String> {

}
