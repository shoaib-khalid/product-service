package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface ClientsRepository extends PagingAndSortingRepository<Client, String>, JpaRepository<Client, String> {

    Client findByUsername(String userName);
    
    Client findByUsernameOrEmail(String userName, String email);
}
