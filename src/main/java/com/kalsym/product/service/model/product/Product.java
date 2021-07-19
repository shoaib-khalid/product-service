package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "product")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String name;

    private String description;

    private String storeId;

    @Column(name = "categoryId")
    private String categoryId;

    private String status;

    private String thumbnailUrl;

    private String vendor;

    private String region;
    
    private String seoUrl;
    
    private String seoName;

    public Product(String name, String description, String storeId, String categoryId, String status, String thumbnailUrl, String vendor, String region, String seoUrl, String seoName) {
        this.name = name;
        this.description = description;
        this.storeId = storeId;
        this.categoryId = categoryId;
        this.status = status;
        this.thumbnailUrl = thumbnailUrl;
        this.vendor = vendor;
        this.region = region;
        this.seoUrl = seoUrl;
        this.seoName = seoName;
    }

    public void update(Product product) {
        if (null != product.getName()) {
            name = product.getName();
        }

        if (null != product.getCategoryId()) {
            categoryId = product.getCategoryId();
        }
        
        if(null!=product.getDescription()){
            description = product.getDescription();
        }
        
        if(null!=product.getStatus()){
            status = product.getStatus();
        }
        if(null!=product.getThumbnailUrl()){
            thumbnailUrl = product.getThumbnailUrl();
        }
             
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Product other = (Product) obj;
        return Objects.equals(this.id, other.getId());
    }

}
