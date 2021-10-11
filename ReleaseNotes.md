##################################################
# product-service-3.2.26 | 11-October-2021
##################################################
### Code Changes:
Add custom pre-authorize to check if session token allowed to access function


##################################################
# product-service-3.2.19 | 27-September-2021
##################################################
### Code Changes:
1. Set default logo & banner url for store asset

### New config:
Url for default logo & banner. Image file need to uploaded manually to server:
store.logo.default.url=https://symplified.biz/store-assets/logo_symplified_bg.png
store.banner.ecommerce.default.url=https://symplified.biz/store-assets/banner-fnb.png
store.banner.fnb.default.url=https://symplified.biz/store-assets/banner-ecomm.jpeg


##################################################
# product-service-3.2.17 | 24-September-2021
##################################################
### Code Changes:
1. Added new features : Store Discount.

### DB Changes:
Add 2 New table :

CREATE TABLE `store_discount` (
  `id` varchar(50) CHARACTER SET utf8 NOT NULL,
  `storeId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `discountName` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `discountType` enum('SHIPPING','TOTALSALES') CHARACTER SET utf8 DEFAULT NULL,
  `isActive` bit(1) DEFAULT NULL,
  `startDate` datetime DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `storeId` (`storeId`),
  CONSTRAINT `store_discount_ibfk_1` FOREIGN KEY (`storeId`) REFERENCES `store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `store_discount_tier` (
  `id` varchar(50) CHARACTER SET utf8 NOT NULL,
  `storeDiscountId` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT 'link to table store_discount',
  `startTotalSalesAmount` decimal(10,2) DEFAULT NULL,
  `endTotalSalesAmount` decimal(10,2) DEFAULT NULL,
  `discountAmount` decimal(10,2) DEFAULT NULL,
  `calculationType` enum('PERCENT','FIX','SHIPAMT') CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'SHIPAMT = shipping amount',
  PRIMARY KEY (`id`),
  KEY `store_discount_tier_ibfk_1` (`storeDiscountId`),
  CONSTRAINT `store_discount_tier_ibfk_1` FOREIGN KEY (`storeDiscountId`) REFERENCES `store_discount` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


##################################################
# product-service-3.2.5 | 15-September-2021
##################################################
### Code Changes:
* Added new URIs for banner and logo delete options.



++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.2.1
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1.sort by created working now
++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.2.0
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1.Sorting bug resolved by using group by
++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.1.7
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1.Tried to solve sort product
++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.1.7
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1.Sort product now working
++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.1.6
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1.Added delete endpoint for deleting all delivery providers related to store
++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.1.5
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1.Default store commission will be added on store creation
++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.1.3 and 4
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. Added properties
2. Added update endpoint in StoreRegionCountrySDeliveryServiceProviderController

++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.1.2
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. Added logging in storelivechat service

++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.1.1
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. Changed urls in store live chat service

++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.1.0
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. Made endpoints for delivery Service provider
2. Made endpoints for linking storeId with delivery service provider
++++++++++++++++++++++++++++++++++++++++++++
+ product-service-3.0.2
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. Solved product search problems
2. Version updated
++++++++++++++++++++++++++++++++++++++++++++
+ product-service-1.0.4
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. When adding asset now, if asset with that endpoint already exists it will be deleted
2. Sorting added in products. Sorting parameter and sortByCol parameter is added.

++++++++++++++++++++++++++++++++++++++++++++
+ product-service-1.0.3
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. Modified the endpoint : POST /store-categories
2. Now when saving categories, user will be able to add images of categories 

++++++++++++++++++++++++++++++++++++++++++++
+ product-service-1.0.2
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. Modified the endpoint : POST /stores/{storeId}/products/{productId}/assets
2. Now thumbnailUrl will not be null if no isThumbnail:true asset is there.  

++++++++++++++++++++++++++++++++++++++++++++
+ product-service-1.0.1
++++++++++++++++++++++++++++++++++++++++++++
+Changes
1. Modified the endpoint : POST /stores/{storeId}/products/{productId}/assets
2. Thumbnail will be set automatically if user does not select it automatically.          

++++++++++++++++++++++++++++++++++++++++++++
+ product-service-1.0-SNAPSHOT
++++++++++++++++++++++++++++++++++++++++++++

Endpoint:
1. Product