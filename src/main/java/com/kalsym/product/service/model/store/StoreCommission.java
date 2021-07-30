package com.kalsym.product.service.model.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import java.sql.Timestamp;
import javax.persistence.GeneratedValue;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 7cu
 */
@Entity
@Table(name = "store_commission")
@Getter
@Setter
@ToString
public class StoreCommission implements Serializable {

    @Id
    private String storeId;

    private Double minChargeAmount;

    private Double rate;

    private int settlementDays;

}
