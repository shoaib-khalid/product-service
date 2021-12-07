package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.Store;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreRepository extends PagingAndSortingRepository<Store, String>, JpaRepository<Store, String> {

    List<Store> findByName(@Param("name") String name);
    
    List<Store> findByDomain(@Param("domain") String domain);

    //List<Product> findByStoreId(@Param("storeId") String storeId);
    //List<Product> findByIdAndName(@Param("Id") String storeId, @Param("name") String name);
    <S extends Object> Page<S> findByClientId(@Param("clientId") String clientId,  Pageable pgbl);
    
    List<Store> findByClientId(@Param("clientId") String clientId);
    
    //@Query("SELECT c FROM Store c WHERE c.isSnooze=true AND c.snoozeEndTime < NOW()")
    //public List<Store> getSnoozeExpired(
    //);

}
