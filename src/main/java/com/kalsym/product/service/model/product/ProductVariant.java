package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "product_variant")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVariant implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    Product product;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "productVariantId")
    private List<ProductVariantAvailable> productVariantsAvailable;
}
