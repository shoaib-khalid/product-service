package com.kalsym.product.service.model.product;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    private String groupId;

   @OneToMany(cascade = CascadeType.ALL,
   fetch = FetchType.LAZY)
   @JoinColumn(name = "addOnItemId",insertable = false, updatable = false, nullable = true)  
   private List<ProductAddOn> productAddOnDetails;



 }