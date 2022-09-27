package com.kalsym.product.service.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Entity
@Table(name = "marketplace_banner_config")
@NoArgsConstructor
public class MarketplaceBannerConfig implements Serializable {

    @Id
    private Integer id;

    private String bannerUrl;

    private String regionCountryId;

    private String type;

    private Integer sequence;

    private Integer delayDisplay;

    private String actionUrl;


}