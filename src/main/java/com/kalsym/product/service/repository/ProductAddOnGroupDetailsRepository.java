package com.kalsym.product.service.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.product.ProductAddOnGroupDetails;



@Repository
public interface ProductAddOnGroupDetailsRepository extends JpaRepository<ProductAddOnGroupDetails, String>,JpaSpecificationExecutor<ProductAddOnGroupDetails>{
    
    // @Query(
    //     " SELECT pagd "
    //     + "FROM ProductAddOnGroupDetails pagd "
    //     + "INNER JOIN ProductAddOnItemDetails paid on paid.groupId = pagd.id "
    //     + "INNER JOIN ProductAddOn pa on pa.addOnItemId = paid.id "
    //     + "WHERE pa.productId = :productId"     
    // )
    // List<ProductAddOnGroupDetails> getProductAddOnJpqlQuery(
    //         @Param("productId") String productId         
    // );
}



