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
            String discountType,
            Boolean isActive, 
            Example<StoreDiscount> example) {

        return (Specification<StoreDiscount>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (from != null && to != null) {
                to.setDate(to.getDate() + 1);
                predicates.add(builder.greaterThanOrEqualTo(root.get("startDate"), from));
                predicates.add(builder.lessThanOrEqualTo(root.get("startDate"), to));
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
