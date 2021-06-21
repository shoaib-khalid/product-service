/*
 * Copyright (C) 2021 saros
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
package com.kalsym.product.service.model.product;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author saros
 */
public class ProductSpecs {

    public static Specification<ProductWithDetails> getProductsSpec(List<String> status, Example<ProductWithDetails> example) {
        return (Specification<ProductWithDetails>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (status == null) {
                predicates.add(builder.equal(root.get("status"), "ACTIVE"));
            } else {
                predicates.add(root.get("status").in(status));

            }
//            if (from != null && to != null) {
//                to.setDate(to.getDate() + 1);
//                predicates.add(builder.greaterThanOrEqualTo(root.get("created"), from));
//                predicates.add(builder.lessThanOrEqualTo(root.get("created"), to));
//            }
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
