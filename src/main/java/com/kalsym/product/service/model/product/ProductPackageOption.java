package com.kalsym.product.service.model.product;

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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
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
@Table(name = "product_package_option")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPackageOption implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String packageId;

    private String title;

    private Integer totalAllow;
     
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "productPackageOptionId", insertable = false, updatable = false, nullable = true)
    private List<ProductPackageOptionDetail> productPackageOptionDetail;
    
    public void update(ProductPackageOption productPackageOption) {
       
        if (null != productPackageOption.getTitle()) {
            title = productPackageOption.getTitle();
        }
       
        if (null != productPackageOption.getTotalAllow()) {
            totalAllow = productPackageOption.getTotalAllow();
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
        final ProductPackageOption other = (ProductPackageOption) obj;
        return Objects.equals(this.id, other.getId());
    }

}
