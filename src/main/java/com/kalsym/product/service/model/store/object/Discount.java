package com.kalsym.product.service.model.store.object;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalsym.product.service.enums.StoreDiscountType;
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

/**
 *
 * @author taufik
 */

@Getter
@Setter
@ToString
public class Discount {
    
    private String id;    
    private String storeId;
    private String discountName;
    
    @Enumerated(EnumType.STRING)
    private StoreDiscountType discountType;
    
    private Boolean isActive;
    private List<StoreDiscountTier> storeDiscountTierList;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime  startTime;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime  endTime;        
}