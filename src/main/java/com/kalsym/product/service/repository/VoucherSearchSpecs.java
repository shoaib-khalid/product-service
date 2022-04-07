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

import com.kalsym.product.service.enums.VoucherStatus;
import com.kalsym.product.service.enums.VoucherType;
import com.kalsym.product.service.model.store.Voucher;
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
public class VoucherSearchSpecs {
     /**
     * Accept two dates and example matcher
     *
     * @param currentDate     
     * @param domain
     * @param storeId
     * @param example
     * @return
     */
    public static Specification<Voucher> getSpecWithDatesBetween(
            Date currentDate, 
            VoucherType voucherType, String storeId,
            Example<Voucher> example) {

        return (Specification<Voucher>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            
            if (currentDate != null) {
                
                //date1
                Predicate predicateForStartDate1 = builder.greaterThanOrEqualTo(root.get("startDate"), currentDate);
                Predicate predicateForEndDate1 = builder.lessThanOrEqualTo(root.get("endDate"), currentDate); 
                Predicate predicateForDate1 = builder.and(predicateForStartDate1, predicateForEndDate1);
                predicates.add(predicateForDate1);
                
                //NOTES : The SQL Server AND operator takes precedence over the SQL Server OR operator (just like a multiplication operation takes precedence over an addition operation).              
            }
            
            if (voucherType!=null) {
                predicates.add(builder.equal(root.get("voucherType"), voucherType));
            } 
            
            if (storeId!=null) {
                predicates.add(builder.equal(root.get("storeId"), storeId));
            } 
            
            predicates.add(builder.equal(root.get("status"), VoucherStatus.ACTIVE));                        
            
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
