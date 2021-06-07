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
@Table(name = "region_country_state")
@ToString
@NoArgsConstructor
public class RegionCountryState implements Serializable {

    @Id
    private String id;

    private String name;
    private String regionCountryId;

    public void update(RegionCountryState regionCountryState) {
        if (null != regionCountryState.getName()) {
            name = regionCountryState.getName();
        }

    }

}
