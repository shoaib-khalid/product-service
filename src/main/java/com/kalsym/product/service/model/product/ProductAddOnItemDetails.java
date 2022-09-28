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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


//this model to select certain column only for viewing purp
 @Getter
 @Setter
 @ToString
 @Entity
 @Table(name = "addon_template_item")
 @JsonInclude(JsonInclude.Include.NON_NULL)

 public class ProductAddOnItemDetails {
    
    @Id 
    private String id;

    private String name;

    private String groupId;

    @OneToOne(cascade = CascadeType.ALL,
    fetch = FetchType.EAGER)
    @JoinColumn(name = "groupId", referencedColumnName = "id", insertable = false, updatable = false, nullable = true)    
    private ProductAddOnGroupDetails productAddOnGroupDetails;

    @Transient
    private String productId;
    
    @Transient
    private Double price;

    @Transient
    private Double dineInPrice;

    @Transient
    private String status;


 }