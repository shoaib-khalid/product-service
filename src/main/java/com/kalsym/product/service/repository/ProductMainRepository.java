package com.kalsym.product.service.repository;


import java.util.List;

import com.kalsym.product.service.model.product.ProductMain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductMainRepository extends JpaRepository<ProductMain,String> {
    
    @Query(
            " SELECT pwd "
            + "FROM ProductMain pwd "
            + "WHERE pwd.storeDetails.regionCountryId = :regionCountryId "
            + "AND pwd.status IN :status "
            + "AND pwd.storeCategory.parentCategoryId = :parentCategoryId"
    )
    Page<ProductMain> getProductCountryIdAndParentCategoryId(
            @Param("status") List<String> status,
            @Param("regionCountryId") String regionCountryId,
            @Param("parentCategoryId") String parentCategoryId,
            Pageable pageable
    );
}

