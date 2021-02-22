package com.kalsym.product.service.model.repository;

import com.kalsym.product.service.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
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
public interface ProductRepository extends PagingAndSortingRepository<Product, String>, JpaRepository<Product, String> {

    List<Product> findByName(@Param("name") String name);

    List<Product> findByStoreId(@Param("storeId") String storeId);

    List<Product> findByStoreIdAndName(@Param("storeId") String storeId, @Param("name") String name);

//    List<Product> findByStoreIdAndName(@Param("storeId") String storeId, @Param("name"), String name);
}
