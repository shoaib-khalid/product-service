/*
 * Copyright (C) 2021 mohsi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kalsym.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.kalsym.product.service.model.store.StoreDeliveryServiceProviderType;
import java.util.List;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author mohsi
 */
@Repository
public interface StoreDeliveryProviderTypeRepository extends PagingAndSortingRepository<StoreDeliveryServiceProviderType, String>,
         JpaRepository<StoreDeliveryServiceProviderType, String> {

    List <StoreDeliveryServiceProviderType> findByRegionCountry(@Param("regionCountry") String regionCountry);
    
    List <StoreDeliveryServiceProviderType> findByRegionCountryAndDeliveryType(@Param("regionCountry") String regionCountry, @Param("deliveryType") String deliveryType);
}
