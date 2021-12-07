/*
 * Copyright (C) 2021 taufik
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kalsym.product.service.repository;

import com.kalsym.product.service.enums.StoreDiscountType;
import com.kalsym.product.service.model.store.StoreDiscount;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
/**
 *
 * @author taufik
 */
public class StoreDiscountSearchSpecs {
     /**
     * Accept two dates and example matcher
     *
     * @param from
     * @param to
     * @param example
     * @return
     */
    public static Specification<StoreDiscount> getSpecWithDatesBetween(
            Date from, Date to, 
            String discountName, 
            StoreDiscountType discountType,
            Boolean isActive, 
            Example<StoreDiscount> example) {

        return (Specification<StoreDiscount>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            
            if (from != null && to != null) {
                to.setDate(to.getDate() + 1);
                
                //date1
                Predicate predicateForStartDate1 = builder.greaterThanOrEqualTo(root.get("startDate"), from);
                Predicate predicateForEndDate1 = builder.lessThanOrEqualTo(root.get("endDate"), to); 
                Predicate predicateForDate1 = builder.and(predicateForStartDate1, predicateForEndDate1);
                
                //date2
                Predicate predicateForStartDate2 = builder.lessThanOrEqualTo(root.get("startDate"), from);
                Predicate predicateForEndDate2 = builder.greaterThanOrEqualTo(root.get("startDate"), to); 
                Predicate predicateForDate2 = builder.and(predicateForStartDate2, predicateForEndDate2);
                
                //date3
                Predicate predicateForStartDate3 = builder.lessThanOrEqualTo(root.get("endDate"), from);
                Predicate predicateForEndDate3 = builder.greaterThanOrEqualTo(root.get("endDate"), to); 
                Predicate predicateForDate3 = builder.and(predicateForStartDate3, predicateForEndDate3);
                
                Predicate finalPredicate1 = builder.or(predicateForDate1, predicateForDate2, predicateForDate3);
               //Predicate finalPredicate2 = builder.or(predicateForDate2, predicateForDate3);
               // Predicate finalPredicate3 = builder.or(predicateForDate2, predicateForDate3);
                
                predicates.add(finalPredicate1);
              //  predicates.add(finalPredicate2);
              //  predicates.add(finalPredicate3);  

              //NOTES : The SQL Server AND operator takes precedence over the SQL Server OR operator (just like a multiplication operation takes precedence over an addition operation).              
            }
            if (discountName!=null) {
                predicates.add(builder.equal(root.get("discountName"), discountName));
            } else if (discountType!=null) {
                predicates.add(builder.equal(root.get("discountType"), discountType));
            } else if (isActive!=null) {
                predicates.add(builder.equal(root.get("isActive"), isActive));
            }
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
