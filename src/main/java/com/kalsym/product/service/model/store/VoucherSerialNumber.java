package com.kalsym.product.service.model.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalsym.product.service.enums.VoucherCurrentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voucherId;
    private Boolean isUsed;

    @Column(unique = true)
    private String serialNumber;

    @Column(unique = true)
    private String voucherRedeemCode;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    private VoucherCurrentStatus currentStatus;

    public void update(VoucherSerialNumber bodyVoucherSerialNumber){
        if(bodyVoucherSerialNumber == null){
            return;
        }

        if(bodyVoucherSerialNumber.getSerialNumber() != null){
            this.setSerialNumber(bodyVoucherSerialNumber.getSerialNumber());
        }

        if(bodyVoucherSerialNumber.getVoucherRedeemCode() != null){
            this.setVoucherRedeemCode(bodyVoucherSerialNumber.getVoucherRedeemCode());
        }


    }

    // Generate a unique redeem code with the format 'ABC1234567ABC'
    public static String generateUniqueRedeemCode(String name, Long id) {
        StringBuilder extractedCharacters = new StringBuilder();

        //Extract the first character from each word in the voucher name
        String[] words = name.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                extractedCharacters.append(word.charAt(0));
            }
        }

        String serialNumber= id.toString();

        //concatenate the extracted characters and the serial number to
        //form the redeem code
        String redeemCode = extractedCharacters.toString().toUpperCase() + serialNumber;

        System.out.println(redeemCode);
        return redeemCode;
    }

    // Generate a unique serial number not exceeding 11 digits
    public static String generateUniqueSerialNumber(String generatedVoucherRedeemCode) {
        StringBuilder serialNumber = new StringBuilder();
        String redeemCode = generatedVoucherRedeemCode.toUpperCase();

        // Iterate through each character in the redeem code
        for (int i = 0; i < redeemCode.length(); i++) {
            char character = redeemCode.charAt(i);
            // Check if the character is a letter or a digit
            if (Character.isLetter(character)) {
                // Append the ASCII value of the letter to the serial number
                serialNumber.append((int) character);
            } else if (Character.isDigit(character)) {
                // Append the digit directly to the serial number
                serialNumber.append(character);
            }
        }

        return serialNumber.toString();
    }



}
