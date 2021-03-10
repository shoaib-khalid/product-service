package com.kalsym.product.service.model.repository;

import com.kalsym.product.service.model.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
//@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
@Repository
public interface StoreCategoryRepository extends PagingAndSortingRepository<StoreCategory, String>, JpaRepository<StoreCategory, String> {

    //List<Category> findByName(@Param("name") String name);
}
