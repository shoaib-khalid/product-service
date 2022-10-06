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


//in order to make frontend easier to manipulate data we will use this model, hence we use transient annotation so that we will set the value of of product add on in this  model 
// do not use this model for create and update
 @Getter
 @Setter
 @ToString
 @Entity
 @Table(name = "addon_template_item")
 @JsonInclude(JsonInclude.Include.NON_NULL)

 public class ProductAddOnItemDetails {
    
    @Id 
    private String id;

    @Transient
    private String addOnItemId;

    private String groupId;

    private String name;

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

    @Transient
    private Integer sequenceNumber;



 }