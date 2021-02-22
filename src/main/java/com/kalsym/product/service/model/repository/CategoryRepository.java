package com.kalsym.product.service.model.repository;

import com.kalsym.product.service.model.Category;
import java.util.List;
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
//@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, String> {

    List<Category> findByName(@Param("name") String name);
}
