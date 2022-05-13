package com.kalsym.product.service.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Sarosh
 */
@Getter
@Setter
@Entity
@Table(name = "delivery_zone_city")
@ToString
@NoArgsConstructor
public class RegionCountryStateCity {
    
    @Id
    private String city;
    private String zone;
    private String costCenterCode;
    private String country;
    private String state;

}
