package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.VoucherVertical;
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
public interface VoucherVerticalRepository extends PagingAndSortingRepository<VoucherVertical, String>, JpaRepository<VoucherVertical, String>, JpaSpecificationExecutor<VoucherVertical> {
    @Transactional
    @Modifying
    @Query("DELETE FROM VoucherVertical vt WHERE vt.voucherId = :voucherId")
    void deleteByVoucherId(@Param("voucherId") String voucherId);

}
