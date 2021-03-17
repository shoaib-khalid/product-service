package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name = "product_inventory_item")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@IdClass(ProductInventoryItemId.class)
public class ProductInventoryItem implements Serializable {

    @Id
    private String itemCode;

    @Id
    private String productVariantAvailableId;

    private String productId;

}
