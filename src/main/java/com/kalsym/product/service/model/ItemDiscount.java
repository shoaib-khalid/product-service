/*
 * Copyright (C) 2021 taufik
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
package com.kalsym.product.service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.time.LocalDateTime;  
import com.kalsym.product.service.enums.DiscountCalculationType;

/**
 *
 * @author taufik
 */
public class ItemDiscount {
    public double normalPrice;
    public double discountedPrice;
    public String discountLabel;
    public double discountAmount;    
    public boolean normalItemOnly;
    public DiscountCalculationType calculationType;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime discountStartTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime discountEndTime;
    
    public Date lastUpdateTime;
}
