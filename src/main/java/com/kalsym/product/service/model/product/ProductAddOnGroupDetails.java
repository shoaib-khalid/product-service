package com.kalsym.product.service.model.product;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
    
    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId",insertable = false, updatable = false, nullable = true)
    private List<ProductAddOnItemDetails> productAddOnItemDetails;
    
}
