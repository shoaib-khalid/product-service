package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.kalsym.product.service.model.ItemDiscount;

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
public class ProductInventoryWithProductDetails implements Serializable {

    @Id
    private String itemCode;

    private Double price;
    private Double compareAtprice;

    private String SKU;

    //private String name;
    private Integer quantity;

    private String productId;
    
    private String status;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "itemCode")
    private List<ProductInventoryItem> productInventoryItems;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false, nullable = true)
    private Product product;
    
    @Transient 
    private ItemDiscount itemDiscount;
}
