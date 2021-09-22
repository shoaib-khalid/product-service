package com.kalsym.product.service.model.store;

import com.kalsym.product.service.model.product.ProductVariant;
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
import java.sql.Time;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "store_discount")
@NoArgsConstructor
public class StoreDiscount implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    
    private String storeId;
    private String discountName;
    private String discountType;
    private Boolean isActive;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time startTime;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time endTime;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeDiscountId", insertable = false, updatable = false, nullable = true)    
    private List<StoreDiscountTier> storeDiscountTierList;

}
