package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.product.ProductPackageOption;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface ProductPackageOptionRepository extends PagingAndSortingRepository<ProductPackageOption, String>, JpaRepository<ProductPackageOption, String> {

    List<ProductPackageOption> findByPackageId(@Param("packageId") String packageId);
    

}
