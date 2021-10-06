package com.kalsym.product.service.model.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kalsym.product.service.model.product.Product;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import java.util.Date;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "store_discount_tier")
@NoArgsConstructor
public class StoreDiscountTier implements Serializable, Comparable< StoreDiscountTier > {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    
    private String storeDiscountId;
    private Double startTotalSalesAmount;
    private Double endTotalSalesAmount;
    private Double discountAmount;
    private String calculationType;
    
    @Override
    public int compareTo(StoreDiscountTier o) {
        return this.getStartTotalSalesAmount().compareTo(o.getStartTotalSalesAmount());
    }

}
