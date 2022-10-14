package com.kalsym.product.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.product.ProductAddOnGroup;


@Repository
public interface ProductAddOnGroupRepository extends JpaRepository<ProductAddOnGroup, String>,JpaSpecificationExecutor<ProductAddOnGroup>{
    
    Optional<ProductAddOnGroup> findByProductIdAndAddonTemplateGroupId(@Param("productId") String productId,@Param("addonTemplateGroupId") String addonTemplateGroupId);

    List<ProductAddOnGroup> findByProductId(@Param("productId") String productId);

    List<ProductAddOnGroup> findByProductIdAndStatusNot(@Param("productId") String productId,@Param("status") String status);

    Optional<ProductAddOnGroup> findByProductIdAndAddonTemplateGroupIdAndStatusNot(@Param("productId") String productId,@Param("addonTemplateGroupId") String addonTemplateGroupId,@Param("status") String status);


}

