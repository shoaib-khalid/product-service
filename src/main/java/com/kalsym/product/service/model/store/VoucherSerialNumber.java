package com.kalsym.product.service.model.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalsym.product.service.enums.VoucherCurrentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;


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
    private static final long MIN_SERIAL_NUMBER = 100000L; // 6 digits (e.g., 100000)
    private static final long MAX_SERIAL_NUMBER = 99999999999L; // 11 digits

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String voucherId;
    private Boolean isUsed;

    @Column(unique = true)
    private long serialNumber;

    @Column(unique = true)
    private String voucherRedeemCode;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    private VoucherCurrentStatus currentStatus;


    // Generate a unique redeem code with the format 'ABC1234567ABC'
    public static String generateUniqueRedeemCode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        Random random = new Random();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        for (int i = 0; i < 7; i++) {
            sb.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        for (int i = 0; i < 3; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return sb.toString();
    }

    // Generate a unique serial number not exceeding 11 digits
    public static long generateUniqueSerialNumber() {
        long serialNumber;

        // Generate a random serial number and ensure it has at least 6 digits
        do {
            serialNumber = MIN_SERIAL_NUMBER + new Random().nextInt() % (MAX_SERIAL_NUMBER - MIN_SERIAL_NUMBER + 1);
        } while (serialNumber <= 0);

        return serialNumber;
    }


}
