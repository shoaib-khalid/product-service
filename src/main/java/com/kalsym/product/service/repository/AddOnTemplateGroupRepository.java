package com.kalsym.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.product.AddOnTemplateGroup;

@Repository
public interface AddOnTemplateGroupRepository extends JpaRepository<AddOnTemplateGroup, String>,JpaSpecificationExecutor<AddOnTemplateGroup>{
    
}
