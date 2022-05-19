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
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;


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
    public Optional<PromoText> getPromoTextById(String id){
        return promoTextRepository.findById(id);
    }

    // Get By Query WITH Pagination
    public Page<PromoText> getByQueryPromoText(int page, int pageSize, String verticalCode, String eventId ){
    
        PromoText PromoTextMatch = new PromoText();
        PromoTextMatch.setVerticalCode(verticalCode);
        PromoTextMatch.setEventId(eventId);

        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withMatcher("verticalCode", new GenericPropertyMatcher().exact())
        .withMatcher("eventId", new GenericPropertyMatcher().exact())
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<PromoText> example = Example.of(PromoTextMatch, matcher);

        Pageable pageable = PageRequest.of(page, pageSize);

        return promoTextRepository.findAll(example,pageable);
    }
}
