package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author 7cu
 */




@Getter
@Setter
@ToString
@Entity
@Table(name = "product_inventory")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductInventory implements Serializable {

    @Id
    public String itemCode;

    public Double price;

    public Double compareAtprice;

    // @JsonProperty("SKU")
    public String SKU;

    //public String name;
    public Integer quantity;

    public String productId;
    
    public String status;

    public Double dineInPrice;
    
    public String barcode;

    public Double costPrice;
    
    public void update(ProductInventory pi) {
        if (null != pi.getPrice()) {
            price = pi.getPrice();
        }        

        if (null != pi.getCompareAtprice()) {
            compareAtprice = pi.getCompareAtprice();
        }

        if (null != pi.getSKU()) {
            SKU = pi.getSKU();
        }
        
        if (null != pi.getBarcode()) {
            barcode = pi.getBarcode();
        }

        if (null != pi.getQuantity()) {
            quantity = pi.getQuantity();
        }

        if (null != pi.getProductId()) {
            productId = pi.getProductId();
        }
        
        if (null != pi.getStatus()) {
            status = pi.getStatus();
        }
               
        if (null != pi.getDineInPrice()) {
            dineInPrice = pi.getDineInPrice();
        }

        if (null != pi.getCostPrice()) {
            costPrice = pi.getCostPrice();
        }

    }
}
