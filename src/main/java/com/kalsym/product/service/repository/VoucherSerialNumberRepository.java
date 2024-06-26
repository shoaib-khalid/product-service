package com.kalsym.product.service.repository;

import com.kalsym.product.service.enums.VoucherCurrentStatus;
import com.kalsym.product.service.model.store.VoucherSerialNumber;

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
public interface VoucherSerialNumberRepository extends PagingAndSortingRepository<VoucherSerialNumber, String>, JpaRepository<VoucherSerialNumber, String>, JpaSpecificationExecutor<VoucherSerialNumber> {
    @Transactional
    @Modifying
    @Query("DELETE FROM VoucherSerialNumber vt WHERE vt.voucherId = :voucherId")
    void deleteByVoucherId(@Param("voucherId") String voucherId);

    VoucherSerialNumber findByVoucherRedeemCode(String voucherRedeemCode);

    @Query("SELECT vt FROM VoucherSerialNumber vt WHERE vt.voucherId = :voucherId AND vt.currentStatus IN ('USED', 'BOUGHT')")
    List<VoucherSerialNumber> findByVoucherToExport(@Param("voucherId") String voucherId);
}
