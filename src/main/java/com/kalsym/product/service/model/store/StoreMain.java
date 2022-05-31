package com.kalsym.product.service.model.store;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//this model use to show certain column only insted of load many column
@Entity
@Table(name = "store")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class StoreMain implements Serializable {

    @Id
    private String id;

    private String name;

    private String city;

    private String storeDescription;

    private String domain;
    
    @Column(name="regionCountryStateId")
    private String state;

    private String postcode;

    private String regionCountryId;
    
    @OneToOne()
    @JoinColumn(name = "id",referencedColumnName="storeId", insertable = false, updatable = false, nullable = true)
    private StoreAsset storeAsset;  

}
