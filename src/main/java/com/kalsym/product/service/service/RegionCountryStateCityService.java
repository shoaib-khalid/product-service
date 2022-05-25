package com.kalsym.product.service.service;
import java.util.List;

import com.kalsym.product.service.model.RegionCountryState;
import com.kalsym.product.service.model.RegionCountryStateCity;
import com.kalsym.product.service.repository.RegionCountryStateCityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.domain.Sort;


@Service
public class RegionCountryStateCityService {
    
    @Autowired
    RegionCountryStateCityRepository regionCountryStateCityRepository; 

    // Get By Query WITH Pagination
    public List<RegionCountryStateCity> getByQueryRegionCountryStateCity(String country, String state, String city,String sortByCol,Sort.Direction sortingOrder){

        RegionCountryState regionCountryStateMatch = new RegionCountryState();
        regionCountryStateMatch.setName(state);
        regionCountryStateMatch.setRegionCountryId(country);

        RegionCountryStateCity RegionCountryStateCityMatch = new RegionCountryStateCity();
        RegionCountryStateCityMatch.setName(city);
        RegionCountryStateCityMatch.setRegionCountryState(regionCountryStateMatch);



        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("country", new GenericPropertyMatcher().exact())
                .withMatcher("state", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<RegionCountryStateCity> example = Example.of(RegionCountryStateCityMatch, matcher);

        Sort sort;

        if (sortingOrder==Sort.Direction.DESC){
            sort = Sort.by(sortByCol).descending();
        }
        else{
            sort = Sort.by(sortByCol).ascending();//Default ascending
        }
        
        return regionCountryStateCityRepository.findAll(example,sort);
    }
}
