package com.kalsym.product.service.repository;

import com.kalsym.product.service.enums.VoucherStatus;
import com.kalsym.product.service.enums.VoucherType;
import com.kalsym.product.service.model.store.VoucherVertical;
import com.kalsym.product.service.model.store.Voucher;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;


/**
 *
 * @author ayaan
 */
public class VoucherSearchSpecs {
    /**
     * Accept two dates and example matcher
     *
     * @param currentDate
     * @param voucherType
     * @param storeId
     * @param verticalCode
     * @param voucherCode
     * @param example
     * @return
     */
    public static Specification<Voucher> getSpecWithDatesBetween(
            Date currentDate,
            VoucherType voucherType,
            String storeId,
            String verticalCode,
            String voucherCode,
            VoucherStatus voucherStatus,
            Example<Voucher> example) {

        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<Voucher, VoucherVertical> voucherVertical = root.join("voucherVerticalList");

            if (currentDate != null) {
                //date1
                Predicate predicateForStartDate1 = builder.greaterThanOrEqualTo(root.get("endDate"), currentDate);
                Predicate predicateForEndDate1 = builder.lessThanOrEqualTo(root.get("startDate"), currentDate);
                Predicate predicateForDate1 = builder.and(predicateForStartDate1, predicateForEndDate1);
                predicates.add(predicateForDate1);

                //NOTES : The SQL Server AND operator takes precedence over the SQL Server OR operator
                //(just like a multiplication operation takes precedence over an addition operation).
            }

            if (voucherType!=null) {
                predicates.add(builder.equal(root.get("voucherType"), voucherType));
            }

            if (storeId!=null) {
                predicates.add(builder.equal(root.get("storeId"), storeId));
            }

            if (verticalCode!=null) {
                predicates.add(builder.equal(voucherVertical.get("verticalCode"), verticalCode));
            }

            if (voucherCode!=null) {
                predicates.add(builder.equal(root.get("voucherCode"), voucherCode));
            }

            if(voucherStatus!=null){
                predicates.add(builder.equal(root.get("voucherStatus"), voucherStatus));
            }

            Predicate predicateForTotalRedeem = builder.lessThanOrEqualTo(root.get("totalRedeem"), root.get("totalQuantity"));
            predicates.add(predicateForTotalRedeem);

            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

}