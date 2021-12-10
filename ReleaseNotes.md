##################################################
# product-service-3.3.0 | 08-December-2021
##################################################

##Code Changes
Buf fix for product package. check if null, set to 0
New function to manage item in store discount : StoreDiscountProduct -> POST, GET, PUT
Add new response parameter in getStoreProducts() & getStoreProductById() to give discounted price on every item : productInventories->itemDiscount
	

##DB Changes
ALTER TABLE `store_discount` ADD normalPriceItemOnly tinyint(1);
ALTER TABLE
    `store_discount`
MODIFY COLUMN
    `discountType` enum(
        'SHIPPING',
        'TOTALSALES',
        'ITEM'
    );

	
CREATE TABLE `store_discount_product` (
  `id` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `storeDiscountId` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `itemCode` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `categoryId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `storeDiscountId` (`storeDiscountId`,`itemCode`),
  UNIQUE KEY `storeDiscountId` (`storeDiscountId`,`categoryId`),
  KEY `itemCode` (`itemCode`),
  KEY `categoryId` (`categoryId`),
  CONSTRAINT `store_discount_product_ibfk_1` FOREIGN KEY (`storeDiscountId`) REFERENCES `store_discount` (`id`),
  CONSTRAINT `store_discount_product_ibfk_2` FOREIGN KEY (`itemCode`) REFERENCES `product_inventory` (`itemCode`),
  CONSTRAINT `store_discount_product_ibfk_3` FOREIGN KEY (`categoryId`) REFERENCES `store_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
>>>>>>> aaffef17b12e4c37171cd941c8ad06ac73828e0c


##################################################
# product-service-3.2.47 | 30-November-2021
##################################################

Add new filter 'deliveryType' in request of getDeliveryServiceProvider()
Add max discount amount for store discount

##DB Changes
ALTER TABLE `store_discount` ADD maxDiscountAmount decimal(10,2);


##################################################
# product-service-3.2.46 | 30-November-2021
##################################################

Add product details in response of getStoreProductInventorysById()
Add relationship in ProductInventoryWithDetails class


##################################################
# product-service-3.2.45 | 29-November-2021
##################################################

Add product details in response of POST & PUT productPackageOption
Add product-inventory in response of GET productPackageOption

	
##################################################
# product-service-3.2.44 | 26-November-2021
##################################################

Bug fix for update domain in store details
Add new function :
	1. putStoreProductInventorysById()
	2. putStoreProductInventoryItemsById()

	
##################################################
# product-service-3.2.43 | 25-November-2021
##################################################

new parameter in product package option details -> product details


##################################################
# product-service-3.2.42 | 22-November-2021
##################################################

New API for store discount : search & pagination ->  searchDiscountByStoreId()


##################################################
# product-service-3.2.41 | 17-November-2021
##################################################

### Code Changes:
New field for store & product
New API to manage product package (combo)
Bug fix for storeDescription character length

### DB Changes:
ALTER TABLE product ADD isPackage TINYINT(1) DEFAULT 0;
ALTER TABLE store ADD googleAnalyticId VARCHAR(50);

CREATE TABLE product_package_option (
id VARCHAR(50) PRIMARY KEY,
packageId VARCHAR(50),
title VARCHAR(100),
totalAllow INT
);


CREATE TABLE product_package_option_detail (
id VARCHAR(50) PRIMARY KEY,
productPackageOptionId VARCHAR(50),
productId VARCHAR(50)
);


##################################################
# product-service-3.2.40 | 16-November-2021
##################################################
### Code Changes:
Bug fix for date format for snoozeStartTime & snoozeEndTime in getStoreSnooze()


##################################################
# product-service-3.2.39 | 16-November-2021
##################################################
### Code Changes:
Put store description max length in config : store.description.length

### Config Changes:
New config : 
store.description.length=300


##################################################
# product-service-3.2.37 | 11-November-2021
##################################################
### Code Changes:
Add snooze start time & end time.
Remove scheduler to check snooze expired. 
Backend will check based on snoozeStartTime & snoozeEndTime to determine isSnooze flag.

New API to get snooze info :
function getStoreSnooze() -> GET /stores/{storeId}/timings/snooze

New API to put store to snooze mode :
function putStoreSnooze() -> PUT /stores/{storeId}/timings/snooze


### DB Changes:
ALTER TABLE `store` DROP COLUMN  isSnooze ;
ALTER TABLE `store` ADD snoozeStartTime timestamp;



##################################################
# product-service-3.2.36 | 11-November-2021
##################################################
### Code Changes:
Add new domain easydukan.co for region South Asia in DB
Each vertical will have own domain
Skip domain creation in godaddy & nginx
Use full domain in store table for field domain
Bug fix for snooze mode

### DB Changes:
ALTER TABLE region_vertical ADD domain VARCHAR(200);


##################################################
# product-service-3.2.35 | 02-November-2021
##################################################
### Code Changes:
Add sort & search by name in function getStore
Bug fix for getProductInventory for product with Variant


##################################################
# product-service-3.2.33 | 01-November-2021
##################################################
### Code Changes:
Change scheduler timer to run every 60 seconds


##################################################
# product-service-3.2.32 | 28-October-2021
##################################################
### Code Changes:
New request & response parameter in store timings API POST, PUT, GET :
breakStartTime
breakEndTime
Add new parameter in store asset POST,PUST,GET : bannerMobile

### DB Changes:
ALTER TABLE `store_timing` ADD breakStartTime VARCHAR(10), ADD breakEndTime VARCHAR(10);
ALTER TABLE `store_asset` ADD bannerMobileUrl VARCHAR(300);


##################################################
# product-service-3.2.31 | 25-October-2021
##################################################
### Code Changes:

1. New function to put store in snooze mode (temporary closed) : PUT /stores/{storeId}/timings/snooze
2. Scheduler to put store off snooze mode when snooze end time reach. Run every minute


##################################################
# product-service-3.2.30 | 22-October-2021
##################################################
### Code Changes:

1. Merchant Portal need to send domain when create new store (parameter : domain)

2. Domain will not be created for branch store (isBranch=true)

3. Group for RocketChat will not be created for branch store

4. New function to check domain availability :
/stores/checkdomain
return http 200 if domain available
return http 409 if domain not available

##################################################
# product-service-3.2.29 | 22-October-2021
##################################################
### Code Changes:
Buf fix 


##################################################
# product-service-3.2.28 | 22-October-2021
##################################################
### Code Changes:
Buf fix for update product
New parameter for store during view, insert & edit : isOnline, isBranch, latitude, longitude
New parameter for product : packingSize (possible value : S, M, L, XL, XXL)

### DB Changes:


1) new field in store table :
	
	ALTER TABLE `store` ADD isSnooze TINYINT(1) DEFAULT 1 COMMENT 'to indicate snooze or not (temporary closed). This flag will take preference over the store timings';
	
	ALTER TABLE `store` ADD snoozeEndTime timestamp COMMENT 'use by backend scheduler to set isSnooze=false when snoozeEndTime reach';

	ALTER TABLE `store` ADD snoozeReason VARCHAR(100);
	
	ALTER TABLE `store` ADD isBranch TINYINT(1) DEFAULT 0 COMMENT 'to indicate branch or head-office';
	
	ALTER TABLE `store` ADD latitude VARCHAR(20), ADD longitude VARCHAR(20);	

2) new vertical :
	
	INSERT INTO `region_vertical` values ('e-commerce-b2b2c','E-commerce','E-commerce for Hero Runcit','SEA','https://symplified.biz/merchant.portal-assets/eCommerce.jpg');
	
	INSERT INTO `order_completion_status_config` SELECT id,'e-commerce-b2b2c',STATUS,storePickup,storeDeliveryType,
	statusSequence, emailToCustomer, emailToStore, requestDelivery, rcMessage, 
	`pushNotificationToMerchat`, customerEmailContent, `storeEmailContent`,
	`rcMessageContent`, `comments`, `created`, `updated`, `storePushNotificationContent`, `storePushNotificationTitle`
	FROM `order_completion_status_config` WHERE `verticalId`='FnB';

3) new field in product :
	ALTER TABLE `product` ADD packingSize VARCHAR(5);
	
##################################################
# product-service-3.2.27 | 15-October-2021
##################################################
### Code Changes:
Add new function to get asset for all store filter by clientId


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