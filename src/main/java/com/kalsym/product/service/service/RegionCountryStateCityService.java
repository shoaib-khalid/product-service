package com.kalsym.product.service.service;
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
    public Page<RegionCountryStateCity> getByQueryRegionCountryStateCity(int page, int pageSize, String country, String state, String city,String sortByCol,Sort.Direction sortingOrder){

        RegionCountryStateCity RegionCountryStateCityMatch = new RegionCountryStateCity();
        RegionCountryStateCityMatch.setCountry(country);
        RegionCountryStateCityMatch.setState(state);
        RegionCountryStateCityMatch.setCity(city);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("country", new GenericPropertyMatcher().exact())
                .withMatcher("state", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<RegionCountryStateCity> example = Example.of(RegionCountryStateCityMatch, matcher);

        Pageable pageable;
        if (sortingOrder==Sort.Direction.ASC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());

        }
        else if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else{
            pageable = PageRequest.of(page, pageSize);
        }
        
        return regionCountryStateCityRepository.findAll(example,pageable);
    }
}
