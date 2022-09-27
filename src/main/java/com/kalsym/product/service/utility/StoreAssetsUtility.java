/*
 * Copyright (C) 2022 taufik
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
package com.kalsym.product.service.utility;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.enums.StoreAssetType;
import com.kalsym.product.service.model.RegionVertical;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.StoreAssets;

import java.util.List;
import java.util.Optional;
import com.kalsym.product.service.repository.StoreAssetsRepository;
import com.kalsym.product.service.repository.RegionVerticalRepository;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author taufik
 */
public class StoreAssetsUtility {

    public static List<com.kalsym.product.service.model.store.StoreAssets> SetDefaultAsset(
            String verticalCode, 
            String storeId, 
            List<com.kalsym.product.service.model.store.StoreAssets> storeAssetsList,
            StoreAssetsRepository storeAssetsRepository,
            RegionVerticalRepository regionVerticalRepository,
            String storeBannerFnbDefaultUrl,
            String storeBannerEcommerceDefaultUrl,
            String storeLogoDefaultUrl,
            String storeFavIconUrlSymplified,
            String storeFavIconUrlDeliverin,
            String storeFavIconUrlEasydukan,
            String assetServiceUrl
            ) {
        
        System.out.println("CHECKING THE ASSET URL"+assetServiceUrl);
        //to set the url asset for existing data  
        for(StoreAssets s:storeAssetsList ){
            //handle null
            if(s.getAssetUrl() != null){
                s.setAssetUrl(assetServiceUrl+s.getAssetUrl());

            } else{
                s.setAssetUrl(null);

            }
        }

        List<com.kalsym.product.service.model.store.StoreAssets> desktopBannerList = storeAssetsRepository.findByStoreIdAndAssetType(storeId, StoreAssetType.BannerDesktopUrl);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "SetDefaultAsset", "desktopBannerList found:"+desktopBannerList.toString());
        if (desktopBannerList.isEmpty()) {
            com.kalsym.product.service.model.store.StoreAssets defaultBanner = new com.kalsym.product.service.model.store.StoreAssets();
            if (verticalCode!=null) {                
                if (verticalCode.toUpperCase().contains("FNB")) {
                    defaultBanner.setAssetUrl(storeBannerFnbDefaultUrl);                
                } else {
                    defaultBanner.setAssetUrl(storeBannerEcommerceDefaultUrl);                
                }
            } else {
                defaultBanner.setAssetUrl(storeBannerEcommerceDefaultUrl);            
            }
            defaultBanner.setAssetType(StoreAssetType.BannerDesktopUrl);
            defaultBanner.setStoreId(storeId);
            storeAssetsList.add(defaultBanner);
        }
        
        List<com.kalsym.product.service.model.store.StoreAssets> mobileBannerList = storeAssetsRepository.findByStoreIdAndAssetType(storeId, StoreAssetType.BannerMobileUrl);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "SetDefaultAsset", "mobileBannerList found:"+mobileBannerList.toString());        
        if (mobileBannerList.isEmpty()) {
            com.kalsym.product.service.model.store.StoreAssets defaultBanner = new com.kalsym.product.service.model.store.StoreAssets();
            if (verticalCode!=null) {                
                if (verticalCode.toUpperCase().contains("FNB")) {
                    defaultBanner.setAssetUrl(storeBannerFnbDefaultUrl);                
                } else {
                    defaultBanner.setAssetUrl(storeBannerEcommerceDefaultUrl);                
                }
            } else {
                defaultBanner.setAssetUrl(storeBannerEcommerceDefaultUrl);            
            }
            defaultBanner.setAssetType(StoreAssetType.BannerMobileUrl);
            defaultBanner.setStoreId(storeId);
            storeAssetsList.add(defaultBanner);
        }
        
        List<com.kalsym.product.service.model.store.StoreAssets> logoList = storeAssetsRepository.findByStoreIdAndAssetType(storeId, StoreAssetType.LogoUrl);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "SetDefaultAsset", "logoList found:"+logoList.toString());        
        if (logoList.isEmpty()) {
            Optional<RegionVertical> regionOpt = regionVerticalRepository.findById(verticalCode);
            if (regionOpt.isPresent()) {  
                storeLogoDefaultUrl = regionOpt.get().getDefaultLogoUrl();
            }
            
            com.kalsym.product.service.model.store.StoreAssets defaultLogo = new com.kalsym.product.service.model.store.StoreAssets();
            defaultLogo.setAssetUrl(storeLogoDefaultUrl);                        
            defaultLogo.setAssetType(StoreAssetType.LogoUrl);
            defaultLogo.setStoreId(storeId);
            storeAssetsList.add(defaultLogo);

        }
        
        List<com.kalsym.product.service.model.store.StoreAssets> FavIconList = storeAssetsRepository.findByStoreIdAndAssetType(storeId, StoreAssetType.FaviconUrl);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "SetDefaultAsset", "FavIconList found:"+FavIconList.toString());        
        if (FavIconList.isEmpty()) {
            com.kalsym.product.service.model.store.StoreAssets defaultFavIcon = new com.kalsym.product.service.model.store.StoreAssets();
            Optional<RegionVertical> regionOpt = regionVerticalRepository.findById(verticalCode);
            if (regionOpt.isPresent()) {  
                RegionVertical regionVertical = regionOpt.get();
                if (regionVertical.getDomain().contains("symplified")) {
                    defaultFavIcon.setAssetUrl(storeFavIconUrlSymplified);                
                } else if (regionVertical.getDomain().contains("deliverin")) {
                    defaultFavIcon.setAssetUrl(storeFavIconUrlDeliverin);
                } else if (regionVertical.getDomain().contains("easydukan")) {
                    defaultFavIcon.setAssetUrl(storeFavIconUrlEasydukan);
                } else {
                    defaultFavIcon.setAssetUrl(storeFavIconUrlDeliverin);                
                }
            } else {
                defaultFavIcon.setAssetUrl(storeFavIconUrlDeliverin);            
            }
            defaultFavIcon.setAssetType(StoreAssetType.FaviconUrl);
            defaultFavIcon.setStoreId(storeId);
            storeAssetsList.add(defaultFavIcon);
        }
        
        return storeAssetsList;
    }
    
}
