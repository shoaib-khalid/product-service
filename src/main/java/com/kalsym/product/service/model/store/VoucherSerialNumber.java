package com.kalsym.product.service.model.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalsym.product.service.enums.VoucherCurrentStatus;
import com.kalsym.product.service.enums.VoucherGroupType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author ayaan
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "voucher_serial_number")
@NoArgsConstructor
public class VoucherSerialNumber implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String voucherId;
    private Boolean isUsed;
    private long serialNumber;

    @Column(unique = true)
    private String voucherRedeemCode;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    private VoucherCurrentStatus currentStatus;

}
