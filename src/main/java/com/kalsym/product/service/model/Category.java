package com.kalsym.product.service.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author 7cu
 */
@Entity
@Getter
@Setter
@ToString
public class Category {

    @Id
    private String id;
    
    private String name;
}
