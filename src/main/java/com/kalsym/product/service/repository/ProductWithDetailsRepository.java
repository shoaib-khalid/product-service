package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductWithDetails;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.kalsym.product.service.model.product.ProductWithDetails;
import org.springframework.data.domain.Sort;

/**
 *
 * @author 7cu
 */
// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
//@RepositoryRestResource(collectionResourceRel = "products", path = "products")
@Repository
public interface ProductWithDetailsRepository extends PagingAndSortingRepository<ProductWithDetails, String>, JpaRepository<ProductWithDetails, String>, JpaSpecificationExecutor<ProductWithDetails> {

    List<ProductWithDetails> findByName(@Param("name") String name);

    List<ProductWithDetails> findByStoreId(@Param("storeId") String storeId);

    List<ProductWithDetails> findByStoreIdAndName(@Param("storeId") String storeId, @Param("name") String name);

    @Query(
            " SELECT pwd "
            + "FROM ProductWithDetails pwd INNER JOIN "
            + "ProductInventoryWithDetails pi ON pwd.id = pi.productId "
            + "WHERE pwd.name LIKE CONCAT('%', :name ,'%') "
            + "AND pwd.seoName LIKE CONCAT('%', :seoName ,'%') "
            + "AND pwd.status IN :status "
            + "AND pwd.storeId = :storeId "
            + "AND pwd.categoryId LIKE CONCAT('%', :categoryId, '%') "
            + "GROUP BY pwd.id"
    )
    Page<ProductWithDetails> findByNameOrSeoNameAscendingOrderByPrice(
            @Param("storeId") String storeId,
            @Param("name") String name,
            @Param("seoName") String seoName,
            @Param("status") List<String> status,
            @Param("categoryId") String categoryId,
            Pageable pageable
    );
}
