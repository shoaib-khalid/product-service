package com.kalsym.product.service.model.product;
import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.kalsym.product.service.model.request.ProductAddOnRequest;
import com.kalsym.product.service.model.request.ProductAddonGroupRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "product_addon_group")
public class ProductAddOnGroup {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String addonGroupId;

    private Integer minAllowed;

    private Integer maxAllowed;

    private Integer sequenceNumber;

    private Boolean isDefault;

    private String productId;

    public static ProductAddOnGroup castReference(ProductAddonGroupRequest reqBody){

        ProductAddOnGroup body = new ProductAddOnGroup();
        //set the id for update data
        if(reqBody.getId() != null){

            body.setId(reqBody.getId());

        }

        body.setProductId(reqBody.getProductId());
        body.setAddonGroupId(reqBody.getAddonGroupId());
        body.setMinAllowed(reqBody.getMinAllowed());
        body.setMaxAllowed(reqBody.getMaxAllowed());
        body.setSequenceNumber(reqBody.getSequenceNumber());
        body.setIsDefault(reqBody.getIsDefault());

        return body;
    }
}
