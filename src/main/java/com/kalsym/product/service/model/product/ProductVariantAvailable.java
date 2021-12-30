package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "product_variant_available")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//@IdClass(ProductVariantAvailableId.class)
public class ProductVariantAvailable implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String value;

    private String productId;

    private String productVariantId;

    private Integer sequenceNumber;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "productVariantId", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    private ProductVariant productVariant;
    
    public void update(ProductVariantAvailable productVariant) {
        if (null != productVariant.getValue()) {
            value = productVariant.getValue();
        }        

        if (null != productVariant.getSequenceNumber()) {
            sequenceNumber = productVariant.getSequenceNumber();
        }
        
        if (null != productVariant.getProductVariantId()) {
            productVariantId = productVariant.getProductVariantId();
        }
    }
}
