/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalsym.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.kalsym.product.service.model.Customer;

/**
 *
 * @author taufik
 */
@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, String>, JpaRepository<Customer, String> {
        
}
