package com.kalsym.product.service.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "platform_delivery_provider")
@Getter
@Setter
@ToString
public class PlatformDeliveryProvider {
    
    @Id
    private Integer id;

    private String providerName;
    
    private String providerImage;

    private String platformId;
}
