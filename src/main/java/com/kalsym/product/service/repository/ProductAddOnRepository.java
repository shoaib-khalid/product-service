package com.kalsym.product.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.product.ProductAddOn;

@Repository
public interface ProductAddOnRepository extends JpaRepository<ProductAddOn, String>,JpaSpecificationExecutor<ProductAddOn>{
    
    List<ProductAddOn> findByProductId(@Param("productId") String productId);

    List<ProductAddOn> findByProductIdAndStatusNot(@Param("productId") String productId,@Param("status") String status);

    List<ProductAddOn> findByProductAddonGroupIdAndStatusNot(@Param("productAddonGroupId") String productAddonGroupId, @Param("status") String status);

    List<ProductAddOn> findTop5ByAddonTemplateItemIdAndStatusNot(@Param("addonTemplateItemId") String addonTemplateItemId, @Param("status") String status);

}
