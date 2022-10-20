package com.kalsym.product.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.product.AddOnTemplateItem;

@Repository
public interface AddOnTemplateItemRepository extends JpaRepository<AddOnTemplateItem, String>,JpaSpecificationExecutor<AddOnTemplateItem>{
    
    List<AddOnTemplateItem> findByGroupIdAndStatusNot(@Param("groupId") String groupId,@Param("status") String status);

}
