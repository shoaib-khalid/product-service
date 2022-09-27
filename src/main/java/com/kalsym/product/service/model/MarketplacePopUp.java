package com.kalsym.product.service.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kalsym.product.service.ProductServiceApplication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Entity
@Table(name = "marketplace_popup_config")
@NoArgsConstructor
public class MarketplacePopUp implements Serializable {

    @Id
    private Integer id;

    private String popupUrl;

    private String regionCountryId;

    private String type;

    private Integer sequence;

    private String actionUrl;

    public String getPopupUrl() {
        if (popupUrl==null)
            return null;
        else
            return ProductServiceApplication.ASSETURL+ popupUrl;
    }


}