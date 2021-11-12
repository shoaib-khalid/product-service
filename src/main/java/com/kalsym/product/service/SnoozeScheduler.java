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
package com.kalsym.product.service;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.utility.Logger;
import com.kalsym.product.service.model.store.Store;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
/**
 *
 * @author taufik
 */

@Component
public class SnoozeScheduler {
    
    @Autowired
    StoreRepository storeRepository;
    
    //@Scheduled(fixedRate = 60000) //remove comment to enable scheduler
    public void checkExpiredSnooze() throws Exception {
        System.out.println("Run Snooze-Scheduler");
        String logprefix = "Snooze-Scheduler";
        //List<Store> storeList = storeRepository.getSnoozeExpired();
        List<Store> storeList = null;
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Start checking snooze. Expired snooze:"+storeList.size());        
        for (int i=0;i<storeList.size();i++) {
            Store store = storeList.get(i);
            //store.setIsSnooze(false);
            storeRepository.save(store);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Store:"+store.getId()+" Update snooze off");
        }
    }
}
