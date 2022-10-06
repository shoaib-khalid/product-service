package com.kalsym.product.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.product.ProductAddOnGroup;


@Repository
public interface ProductAddOnGroupRepository extends JpaRepository<ProductAddOnGroup, String>,JpaSpecificationExecutor<ProductAddOnGroup>{
    
    List<ProductAddOnGroup> findByProductId(@Param("productId") String productId);

}

