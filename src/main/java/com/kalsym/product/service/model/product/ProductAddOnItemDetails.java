package com.kalsym.product.service.model.product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


//this model to select certain column only for viewing purp
 @Getter
 @Setter
 @ToString
 @Entity
 @Table(name = "addon_template_item")
 public class ProductAddOnItemDetails {
    
    @Id 
    private String id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL,
    fetch = FetchType.EAGER)
    @JoinColumn(name = "groupId", referencedColumnName = "id", insertable = false, updatable = false, nullable = true)    
    private ProductAddOnGroupDetails productAddOnGroupDetails;



 }