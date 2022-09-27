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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "product_addon")
public class ProductAddOn implements Serializable{
         
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    
    private String productId;

    private String addOnItemId;

    private Double price;

    private Double dineInPrice;

    private String status;

    public static ProductAddOn castReference(ProductAddOnRequest reqBody){

        ProductAddOn body = new ProductAddOn();
        //set the id for update data
        if(reqBody.getId() != null){

            body.setId(reqBody.getId());

        }

        // if new client for delivery, we auto set the dine in price reduce 15%
        if (reqBody.getDineInPrice()==null) {
            body.setDineInPrice(reqBody.getPrice()*0.85);
        } else{
            body.setDineInPrice(reqBody.getDineInPrice());
        }

        // if new client for dinein we auto set for delivery price  Increase 17.5%
        if (reqBody.getPrice()==null) {
            body.setPrice(reqBody.getDineInPrice()*1.175);
        } else{
            body.setPrice(reqBody.getPrice());

        }

        body.setProductId(reqBody.getProductId());
        body.setAddOnItemId(reqBody.getAddOnItemId());
        body.setStatus(reqBody.getStatus());

        return body;
    }
}
