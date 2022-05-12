package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.PromoText;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoTextRepository extends JpaRepository<PromoText, String> {
    
}
