package com.kalsym.product.service.model.store;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author ayaan
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "voucher_vertical")
@NoArgsConstructor
public class VoucherVertical implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String voucherId;
    private String verticalCode;

//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "verticalCode", insertable = false, updatable = false)
//    private RegionVertical regionVertical;

}