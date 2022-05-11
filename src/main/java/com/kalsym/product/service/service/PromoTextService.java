package com.kalsym.product.service.service;

import java.util.List;
import java.util.Optional;

import com.kalsym.product.service.model.PromoText;
import com.kalsym.product.service.repository.PromoTextRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PromoTextService {
    
    //Dependency Injection
    @Autowired
    PromoTextRepository promoTextRepository; 

    // READ
    public List<PromoText> getPromoText() {
        return promoTextRepository.findAll();
    }

    // READ by id
    public Optional<PromoText> getPromoTextById(String eventId){
        return promoTextRepository.findById(eventId);
    }

    // Get By Query WITH Pagination
    public Page<PromoText> getByQueryPromoText(int page, int pageSize){
    
        Pageable pageable = PageRequest.of(page, pageSize);

        return promoTextRepository.findAll(pageable);
    }
}
