package com.kalsym.product.service.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "product_review")
@NoArgsConstructor
//Using IdClass annotation to resolve the composite PK problem in hibernate
@IdClass(ProductReviewId.class)
public class ProductReview implements Serializable {

//    @Id
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "productId", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    private Product product;
    
    @Id 
    private String productId;

    @Id
    private String customerId;

    private int rating;

    private String review;

}
