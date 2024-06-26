package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.Product;
import java.util.List;
import java.util.Optional;

import com.kalsym.product.service.model.store.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
//@RepositoryRestResource(collectionResourceRel = "products", path = "products")
@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, String>, JpaRepository<Product, String>,JpaSpecificationExecutor<Product>, CustomRepository<Product, String>{

    List<Product> findByName(@Param("name") String name);
    
    List<Product> findByNameAndStoreIdAndStatusNot(@Param("name") String name, @Param("storeId") String storeId, @Param("status") String status);

    List<Product> findByStoreId(@Param("storeId") String storeId);

    List<Product> findByStoreIdAndName(@Param("storeId") String storeId, @Param("name") String name);
    
    List<Product> findByCategoryId(@Param("categoryId") String categoryId);

    List<Product> findByStoreIdAndStatusNot(@Param("storeId") String storeId,@Param("status") String status);

    @Query(
        " SELECT p FROM Product p WHERE storeId = :storeId AND status != :status")
    Page<Product> findPageableStoreAndStatus(@Param("storeId") String storeId, @Param("status") String status, Pageable pageable);

    Optional<Product> findByVoucherId(String id);
}
