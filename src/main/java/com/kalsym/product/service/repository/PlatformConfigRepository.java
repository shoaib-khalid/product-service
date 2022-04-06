package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.PlatformConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 *
 * @author Sarosh
 */
@Repository
public interface PlatformConfigRepository extends PagingAndSortingRepository<PlatformConfig, String>, JpaRepository<PlatformConfig, String> {

    List<PlatformConfig> findByDomain(String domain);
       
}
