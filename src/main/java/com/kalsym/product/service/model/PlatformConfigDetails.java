package com.kalsym.product.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalsym.product.service.ProductServiceApplication;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
@Entity
@Table(name = "platform_config_details")
@Getter
@Setter
@ToString
public class PlatformConfigDetails {
    
    @Id
    private String platformId;

    private String whatsappUrl;
    
    private String fbUrl;

    private String instaUrl;

    private String phoneNumber;

    private String email;

    private String adsImageUrl;

    private String address;

    private String businessReg;

    private String actionAdsUrl;

}
