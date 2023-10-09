package com.kalsym.product.service.model.store.object;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalsym.product.service.enums.VoucherCurrentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@ToString
public class VoucherRedeemDTO implements Serializable {
    private String voucherId;
    private String voucherName;
    private Double discountValue;
    private String voucherCode;
    private String currencyLabel;
    private Long serialNumberId;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date redeemDate;

    private VoucherCurrentStatus currentStatus;
    private String serialNumber;
    private String voucherRedeemCode;
    private String thumbnailUrl;

    public VoucherRedeemDTO (String voucherId, String voucherName,
                             Double discountValue, String voucherCode,
                             String currencyLabel, Long serialNumberId,
                             VoucherCurrentStatus currentStatus,
                             String serialNumber, String voucherRedeemCode,
                             Date redeemDate, String thumbnailUrl) {

        this.voucherId = voucherId;
        this.voucherName = voucherName;
        this.discountValue = discountValue;
        this.voucherCode = voucherCode;
        this.currencyLabel = currencyLabel;
        this.serialNumberId = serialNumberId;
        this.currentStatus = currentStatus;
        this.serialNumber = serialNumber;
        this.voucherRedeemCode = voucherRedeemCode;
        this.redeemDate = redeemDate;
        this.thumbnailUrl = thumbnailUrl;
    }

}
