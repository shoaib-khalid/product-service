package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.product.service.enums.ProductStatus;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    private String itemCode;

    private Double price;
    private Double compareAtprice;

    private String SKU;

    //private String name;
    private Integer quantity;

    private String productId;
    
<<<<<<< HEAD
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

=======
    private String status;
>>>>>>> 7b26b61de7190328949bf9a9c8002fea697ddc16
}
