package com.kalsym.product.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.MarketplacePopUp;
import com.kalsym.product.service.repository.MarketplacePopUpRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

@Service
public class MarketplacePopUpService {

    @Autowired
    MarketplacePopUpRepository marketplacePopUpRepository;
    
    public Page<MarketplacePopUp> getMarketPlacePopUpConfig(String regionCountryId,String type,int page, int pageSize,String sortByCol, Sort.Direction sortingOrder){

        MarketplacePopUp marketplacePopUpMatch = new MarketplacePopUp();

        Pageable pageable =PageRequest.of(page, pageSize);

        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<MarketplacePopUp> example = Example.of(marketplacePopUpMatch, matcher);
        
        
        Specification<MarketplacePopUp> marketplacePopUpSpecs = searchMarketplacePopup(regionCountryId,type,sortByCol,sortingOrder,example);
        Page<MarketplacePopUp> result = marketplacePopUpRepository.findAll(marketplacePopUpSpecs, pageable); 

        return result;

    }

    public List<MarketplacePopUp> getListMarketplacePopup(String regionCountryId,String type,int page, int pageSize,String sortByCol, Sort.Direction sortingOrder){

        MarketplacePopUp marketplacePopUpMatch = new MarketplacePopUp();
        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<MarketplacePopUp> example = Example.of(marketplacePopUpMatch, matcher);

        Pageable pageable =PageRequest.of(page, pageSize);

        Specification<MarketplacePopUp> marketplacePopUpSpecs = searchMarketplacePopup(regionCountryId,type,sortByCol,sortingOrder,example);
        Page<MarketplacePopUp> result = marketplacePopUpRepository.findAll(marketplacePopUpSpecs, pageable); 

        List<MarketplacePopUp> marketplacepopupList = result.getContent();
        return marketplacepopupList;
        
    }

    public static Specification<MarketplacePopUp> searchMarketplacePopup(
        String regionCountryId,String type,
        String sortByCol, Sort.Direction sortingOrder,
        Example<MarketplacePopUp> example) {

        return (Specification<MarketplacePopUp>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            
            if (regionCountryId != null && !regionCountryId.isEmpty()) {
                predicates.add(builder.equal(root.get("regionCountryId"), regionCountryId));
            }

            if (type != null && !type.isEmpty()) {
                predicates.add(builder.equal(root.get("type"), type));
            }       
               
            List<Order> orderList = new ArrayList<Order>();
            
            if (sortingOrder==Sort.Direction.ASC){
                orderList.add(builder.asc(root.get(sortByCol)));

            }else{
                orderList.add(builder.desc(root.get(sortByCol)));

            }

            query.orderBy(orderList);
            
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
    
     
}