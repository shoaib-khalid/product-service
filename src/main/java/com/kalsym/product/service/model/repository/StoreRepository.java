package com.kalsym.product.service.model.repository;

import com.kalsym.product.service.model.Store;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */

//@RepositoryRestResource(collectionResourceRel = "stores", path = "stores")
@Repository
public interface StoreRepository extends PagingAndSortingRepository<Store, String> {

    List<Store> findByName(@Param("name") String name);
}


