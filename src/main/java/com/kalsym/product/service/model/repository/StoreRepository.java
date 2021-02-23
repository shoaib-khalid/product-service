package com.kalsym.product.service.model.repository;

import com.kalsym.product.service.model.Product;
import com.kalsym.product.service.model.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreRepository extends PagingAndSortingRepository<Store, String>, JpaRepository<Store, String> {

    List<Store> findByName(@Param("name") String name);

    //List<Product> findByStoreId(@Param("storeId") String storeId);
    //List<Product> findByIdAndName(@Param("Id") String storeId, @Param("name") String name);
    List<Product> findByUserId(@Param("userId") String userId);
}
