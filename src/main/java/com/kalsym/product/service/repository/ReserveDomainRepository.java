package com.kalsym.product.service.repository;

import com.kalsym.product.service.model.ReserveDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;


/**
 *
 * @author 7cu
 */
@Repository
public interface ReserveDomainRepository extends JpaRepository<ReserveDomain,Integer> {

   // Optional<ReserveDomain> findByDomain(@Param("domain") String domain);
   

   @Query(
      " SELECT rd "
      + "FROM ReserveDomain rd "
      + "WHERE rd.domain LIKE CONCAT('%', :domain ,'%')"
   )
   Optional<ReserveDomain> getReserveDomain(
   @Param("domain") String domain
   );

}
