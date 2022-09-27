package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.PlatformOgTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlatformOgTagRepository extends JpaRepository<PlatformOgTag,Integer> {
    
    List<PlatformOgTag> findByPlatformId(@Param("platformId") String platformId);

}
