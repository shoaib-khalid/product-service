package com.kalsym.product.service.model.store;

import com.kalsym.product.service.enums.*;
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
    private Boolean isGlobalStore;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    @Enumerated(EnumType.STRING)
    private VoucherType voucherType;

    @Enumerated(EnumType.STRING)
    private VoucherDiscountType discountType;

    @Enumerated(EnumType.STRING)
    private VoucherGroupType groupType;

    @Enumerated(EnumType.STRING)
    private DiscountCalculationType calculationType;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created_at;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updated_at;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherTerms> voucherTerms;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherVertical> voucherVerticalList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherStore> voucherStoreList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherServiceType> voucherServiceTypeList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherSerialNumber> voucherSerialNumber;


    // Implement the getter method for voucherServiceTypeList
    public List<VoucherServiceType> getVoucherServiceTypeList() {
        return voucherServiceTypeList;
    }


    public void update(Voucher bodyVoucher) {
        if (bodyVoucher == null) {
            return;
        }

        // Update fields only if they are not null in the bodyVoucher
        if (bodyVoucher.getStoreId() != null) {
            this.setStoreId(bodyVoucher.getStoreId());
        }
        if (bodyVoucher.getName() != null) {
            this.setName(bodyVoucher.getName());
        }
        if (bodyVoucher.getDiscountValue() != null) {
            this.setDiscountValue(bodyVoucher.getDiscountValue());
        }
        if (bodyVoucher.getMaxDiscountAmount() != null) {
            this.setMaxDiscountAmount(bodyVoucher.getMaxDiscountAmount());
        }
        if (bodyVoucher.getVoucherCode() != null) {
            this.setVoucherCode(bodyVoucher.getVoucherCode());
        }
        if (bodyVoucher.getTotalQuantity() != null) {
            this.setTotalQuantity(bodyVoucher.getTotalQuantity());
        }
        if (bodyVoucher.getTotalRedeem() != null) {
            this.setTotalRedeem(bodyVoucher.getTotalRedeem());
        }
        if (bodyVoucher.getCurrencyLabel() != null) {
            this.setCurrencyLabel(bodyVoucher.getCurrencyLabel());
        }
        if (bodyVoucher.getIsNewUserVoucher() != null) {
            this.setIsNewUserVoucher(bodyVoucher.getIsNewUserVoucher());
        }
        if (bodyVoucher.getCheckTotalRedeem() != null) {
            this.setCheckTotalRedeem(bodyVoucher.getCheckTotalRedeem());
        }
        if (bodyVoucher.getMinimumSpend() != null) {
            this.setMinimumSpend(bodyVoucher.getMinimumSpend());
        }
        if (bodyVoucher.getAllowDoubleDiscount() != null) {
            this.setAllowDoubleDiscount(bodyVoucher.getAllowDoubleDiscount());
        }
        if (bodyVoucher.getRequireToClaim() != null) {
            this.setRequireToClaim(bodyVoucher.getRequireToClaim());
        }
        if (bodyVoucher.getStatus() != null) {
            this.setStatus(bodyVoucher.getStatus());
        }
        if (bodyVoucher.getVoucherType() != null) {
            this.setVoucherType(bodyVoucher.getVoucherType());
        }
        if (bodyVoucher.getDiscountType() != null) {
            this.setDiscountType(bodyVoucher.getDiscountType());
        }
        if (bodyVoucher.getCalculationType() != null) {
            this.setCalculationType(bodyVoucher.getCalculationType());
        }
        if (bodyVoucher.getStartDate() != null) {
            this.setStartDate(bodyVoucher.getStartDate());
        }
        if (bodyVoucher.getEndDate() != null) {
            this.setEndDate(bodyVoucher.getEndDate());
        }
    }
}