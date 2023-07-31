package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.Voucher;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ayaan
 */

@Repository
public interface VoucherRepository extends PagingAndSortingRepository<Voucher, String>, JpaRepository<Voucher, String>, JpaSpecificationExecutor<Voucher>, CustomRepository<Voucher, String> {

    @Query("SELECT m FROM Voucher m WHERE "
            + "m.voucherCode = :voucherCode "
            + "AND m.storeId = :storeId "
            + "AND m.status='ACTIVE' "
            + "AND m.startDate < :currentDate AND m.endDate > :currentDate "
            + "AND m.totalRedeem < m.totalQuantity")
    Voucher findAvailableVoucherByCode(
            @Param("voucherCode") String voucherCode,
            @Param("currentDate") Date currentDate
    );


    Voucher findByVoucherCode(@Param("voucherCode") String voucherCode);

    /**
     * clear cart item
     * @param voucherId
     */
    @Transactional
    @Modifying
    @Query("UPDATE Voucher m SET m.totalRedeem = m.totalRedeem+1 WHERE m.id = :voucherId")
    public void deductVoucherBalance(
            @Param("voucherId") String voucherId
    );

    /**
     * clear cart item
     * @param voucherId
     */
    @Transactional
    @Modifying
    @Query("UPDATE Voucher m SET m.totalRedeem = m.totalRedeem-1 WHERE m.id = :voucherId")
    public void addVoucherBalance(
            @Param("voucherId") String voucherId
    );

    @Query("SELECT m FROM Voucher m WHERE "
            + "m.isNewUserVoucher = true "
            + "AND m.status='ACTIVE' "
            + "AND m.startDate < :currentDate AND m.endDate > :currentDate ")
    List<Voucher> findAvailableNewUserVoucher(
            @Param("currentDate") Date currentDate
    );


}