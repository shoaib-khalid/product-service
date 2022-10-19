package com.kalsym.product.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.product.AddOnTemplateGroup;

@Repository
public interface AddOnTemplateGroupRepository extends JpaRepository<AddOnTemplateGroup, String>,JpaSpecificationExecutor<AddOnTemplateGroup>{
    
    List<AddOnTemplateGroup> findByStoreIdAndStatusNot(@Param("storeId") String storeId,@Param("status") String status);

}
