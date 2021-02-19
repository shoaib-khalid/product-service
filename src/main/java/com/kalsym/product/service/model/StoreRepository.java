package com.kalsym.product.service.model;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 *
 * @author 7cu
 */

//@RepositoryRestResource(collectionResourceRel = "stores", path = "stores")
public interface StoreRepository{// extends PagingAndSortingRepository<Store, String> {

    List<Store> findByName(@Param("name") String name);
}


