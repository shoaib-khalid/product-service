package com.kalsym.product.service.model.product;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Table(name = "addon_template_group")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ProductAddOnGroupDetails {
    
    @Id
    private String id;

    private String title;
    
    @Transient 
    private List<ProductAddOnItemDetails> productAddOnItemDetail;

    @Transient
    private Integer sequenceNumber;

    @Transient
    private Integer minAllowed;

    @Transient
    private Integer maxAllowed;


}
