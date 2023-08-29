package com.kalsym.product.service.repository;

import com.kalsym.product.service.enums.VoucherStatus;
import com.kalsym.product.service.enums.VoucherType;
import com.kalsym.product.service.model.product.ProductInventoryWithDetails;
import com.kalsym.product.service.model.product.ProductWithDetails;
import com.kalsym.product.service.model.store.VoucherVertical;
import com.kalsym.product.service.model.store.Voucher;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
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

    public static Specification<ProductWithDetails> getProductVoucherSpec(
            String name, String storeId,
            List<String> statusList, Example<ProductWithDetails> example,
            String sortByCol, Sort.Direction sortingOrder) {

        return (Specification<ProductWithDetails>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<ProductWithDetails, ProductInventoryWithDetails> productInventories = root.join("productInventories", JoinType.INNER);
//            Join<ProductWithDetails, Voucher> voucherJoin = root.join("voucherId");

            if (name != null) {
                // predicates.add(builder.equal(root.get("name"), name));
                predicates.add(builder.like(root.get("name"), "%"+name+"%"));

            }

            if (storeId != null ){
                predicates.add(builder.equal(root.get("storeId"), storeId));
            }

            if (statusList!=null) {
                int statusCount = statusList.size();
                List<Predicate> statusPredicatesList = new ArrayList<>();
                for (int i=0;i<statusList.size();i++) {
                    Predicate predicateForCompletionStatus = builder.equal(root.get("status"), statusList.get(i));
                    statusPredicatesList.add(predicateForCompletionStatus);
                }
                Predicate finalPredicate = builder.or(statusPredicatesList.toArray(new Predicate[statusCount]));
                predicates.add(finalPredicate);
            }

            List<Order> orderList = new ArrayList<Order>();

            if (sortingOrder==Sort.Direction.ASC){
                if(sortByCol.equals("price")){

                    orderList.add(builder.asc(productInventories.get(sortByCol)));

                } else if(sortByCol.equals("dineInPrice")){
                    orderList.add(builder.asc(productInventories.get(sortByCol)));

                }
                else{
                    orderList.add(builder.asc(root.get(sortByCol)));

                }

            }else{

                if(sortByCol.equals("price")){

                    orderList.add(builder.desc(productInventories.get(sortByCol)));


                }else if(sortByCol.equals("dineInPrice")){
                    orderList.add(builder.desc(productInventories.get(sortByCol)));

                }
                else{
                    orderList.add(builder.desc(root.get(sortByCol)));

                }


            }

            predicates.add(builder.isNotNull(root.get("voucherId")));

            query.orderBy(orderList);
            query.distinct(true);


            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}