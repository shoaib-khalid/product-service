package com.kalsym.product.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "product_option")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductOption {

    @Id
    private String id;

    private String name;

    private String imageUrl;
    private int stock;
    private String type;
    private String optionCategory;

    private String productId;

}
