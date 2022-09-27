package com.kalsym.product.service.model.product;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
//this model to select certain column only for viewing purp

@Getter
@Setter
@ToString
@Entity
@Table(name = "addon_template_group")
public class ProductAddOnGroupDetails {
    
    @Id
    private String id;

    private String title;

}
