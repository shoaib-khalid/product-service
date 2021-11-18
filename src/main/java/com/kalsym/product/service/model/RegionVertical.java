/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalsym.product.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.product.service.model.product.ProductWithDetails;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Sarosh
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "region_vertical")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegionVertical implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String code;

    private String name;
    private String description;
    private String regionId;
    private String thumbnailUrl;
    private String domain;
    
    public void update(RegionVertical regionVertical) {
        if (null != regionVertical.getName()) {
            name = regionVertical.getName();
        }

        if (null != regionVertical.getDescription()) {
            description = regionVertical.getDescription();
        }

        if (null != regionVertical.getRegionId()) {
            regionId = regionVertical.getRegionId();
        }
        
        if (null != regionVertical.getDomain()) {
            domain = regionVertical.getDomain();
        }
    }

}
