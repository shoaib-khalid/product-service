package com.kalsym.product.service.model.store.object;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalsym.product.service.enums.StoreDiscountType;
import com.kalsym.product.service.model.store.StoreAssets;
import com.kalsym.product.service.model.store.StoreDiscountTier;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import com.kalsym.product.service.model.store.StoreWithDetails;
/**
 *
 * @author taufik
 */

@Getter
@Setter
@ToString
public class TopStore {
    
    private Integer totalStore;    
    private List<StoreAssets> topStoreAsset;
    
}