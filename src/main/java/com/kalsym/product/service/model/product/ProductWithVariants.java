package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductWithVariants implements Serializable {
    private Product product;

    private List<ProductVariant> productVariants = new ArrayList<>();
}

