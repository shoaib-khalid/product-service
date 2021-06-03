package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface ProductReviewRepository extends PagingAndSortingRepository<ProductReview, String>, JpaRepository<ProductReview, String> {

    <S extends Object> Page<S> findByProductId(@Param("productId") String productId, Pageable pgbl);
}
