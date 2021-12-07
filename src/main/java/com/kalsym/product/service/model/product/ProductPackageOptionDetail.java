package com.kalsym.product.service.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "product_package_option_detail")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPackageOptionDetail implements Serializable {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    
    private String productPackageOptionId;
    
    private String productId;
   
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName="productId", insertable = false, updatable = false)
    private List<ProductInventory> productInventory;
}
