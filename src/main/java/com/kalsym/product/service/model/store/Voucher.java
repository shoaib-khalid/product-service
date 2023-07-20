package com.kalsym.product.service.model.store;

import com.kalsym.product.service.enums.VoucherDiscountType;
import com.kalsym.product.service.enums.DiscountCalculationType;
import com.kalsym.product.service.enums.VoucherStatus;
import com.kalsym.product.service.enums.VoucherType;
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
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;


/**
 *
 * @author ayaan
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "voucher")
@NoArgsConstructor
public class Voucher implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String storeId;
    private String name;

    private Double discountValue;
    private Double maxDiscountAmount;
    private String voucherCode;
    private Integer totalQuantity;
    private Integer totalRedeem;
    private String currencyLabel;
    private Boolean isNewUserVoucher;
    private Boolean checkTotalRedeem;
    private Double minimumSpend;
    private Boolean allowDoubleDiscount;
    private Boolean requireToClaim;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    @Enumerated(EnumType.STRING)
    private VoucherType voucherType;

    @Enumerated(EnumType.STRING)
    private VoucherDiscountType discountType;

    @Enumerated(EnumType.STRING)
    private DiscountCalculationType calculationType;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherTerms> voucherTerms;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherVertical> voucherVerticalList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherStore> voucherStoreList;

}