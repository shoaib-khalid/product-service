package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.store.StoreDiscount;
import com.kalsym.product.service.model.store.Voucher;
import java.util.List;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */

@Repository
public interface VoucherRepository extends PagingAndSortingRepository<Voucher, String>, JpaRepository<Voucher, String>, JpaSpecificationExecutor<Voucher> {

}
