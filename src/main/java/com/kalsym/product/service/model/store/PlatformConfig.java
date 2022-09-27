package com.kalsym.product.service.model.store;

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

/**
 *
 * @author Sarosh
 */
@Entity
@Table(name = "platform_config")
@Getter
@Setter
@ToString
public class PlatformConfig implements Serializable {
    
    @Id
    private String platformId;

    private String platformName;

    private String platformLogo;

    private String platformLogoDark;

    private String platformFavIcon;

    private String platformType;

    private String platformCountry;

    private String domain;
    
    private String gaCode;
    
    private String platformFavIcon32;

    private String platformLogoSquare;

    
    public String getPlatformLogo() {
        if (platformLogo==null)
            return null;
        else
            return ProductServiceApplication.ASSETURL+ platformLogo;
    }

    public String getPlatformLogoDark() {
        if (platformLogoDark==null)
            return null;
        else
            return ProductServiceApplication.ASSETURL+ platformLogoDark;
    }

    public String getPlatformFavIcon() {
        if (platformFavIcon==null)
            return null;
        else
            return ProductServiceApplication.ASSETURL+ platformFavIcon;
    }

    public String getPlatformFavIcon32() {
        if (platformFavIcon32==null)
            return null;
        else
            return ProductServiceApplication.ASSETURL+ platformFavIcon32;
    }

    public String getPlatformLogoSquare() {
        if (platformLogoSquare==null)
            return null;
        else
            return ProductServiceApplication.ASSETURL+ platformLogoSquare;
    }

}
