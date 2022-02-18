##################################################
# product-service-3.5.1 | 18-Feb-2022
##################################################
add new field in discount : isExpired
if discount end date < current date, then isExpired=true


##################################################
# product-service-3.5.0 | 17-Feb-2022
##################################################
New function to get store supported delivery period:
GET & POST /stores/{storeId}/deliveryperiods

delivery period available:
EXPRESS - within 2 hours
FOURHOURS - within 4 hours
NEXYTDAY - - next day delivery
FOURDAYS - within 4 days

##Database changes :
CREATE TABLE store_delivery_period (
id VARCHAR(50),
storeId VARCHAR(50),
deliveryPeriod ENUM('EXPRESS','FOURHOURS','NEXTDAY','FOURDAYS'),
enabled TINYINT(1)
);


##################################################
# product-service-3.4.7 | 14-Feb-2022
##################################################
Bug fix for POST store assets
Bug fix for GET /stores/{storeId}/products to return all product even without inventories & category
New function to get top store : GET /stores/top
Bug fix for POST store assets :  generateRandomName() filename

##New config :
store.favicon.easydukan.default.url=https://symplified.it/store-assets/fav-icon-easydukan.png
store.favicon.deliverin.default.url=https://symplified.it/store-assets/fav-icon-deliverin.png
store.favicon.symplified.default.url=https://symplified.it/store-assets/fav-icon-symplified.png

##Database changes :
ALTER TABLE `store` ADD created TIMESTAMP DEFAULT NOW();
ALTER TABLE `store` ADD updated TIMESTAMP  DEFAULT NOW();
ALTER TABLE `product` ADD vehicleType ENUM('MOTORCYCLE','CAR','VAN','PICKUP','LARGEVAN','SMALLLORRY','MEDIUMLORRY','LARGELORRY');


##################################################
# product-service-3.4.6 | 9-Feb-2022
##################################################
Bug fix for delete store by id


##################################################
# product-service-3.4.5 | 28-Jan-2022
##################################################
Bug fix for store assets
Bug fix for product variant by bulk

##################################################
# product-service-3.4.4 | 26-Jan-2022
##################################################
1. Add new API for product variant :
	deleteStoreProductVariantsByBulk
	
1. Add new API for product variant available :
	postStoreProductVariantsAvailableByBulk
	putStoreProductVariantsAvailableByBulk
	deleteStoreProductVariantsAvailableByBulk
	
	
##################################################
# product-service-3.4.3 | 20-Jan-2022
##################################################

1. Add new API for product variant :
	postStoreProductVariantsByBulk
	putStoreProductVariantsByBulk

2. New structure for store asset to support multiple store banner

3. New API for storeAssets

4. Function to add discount banner

#DB Changes:
CREATE TABLE store_assets (
id VARCHAR(50) PRIMARY KEY,
storeId VARCHAR(50),
assetUrl VARCHAR(200),
assetDescription VARCHAR(200),
assetType ENUM ('BannerDesktopUrl','BannerMobileUrl','FaviconUrl','DiscountBannerUrl','QrcodeUrl','LogoUrl')
);

ALTER TABLE store_discount ADD bannerId VARCHAR(50);


##################################################
# product-service-3.4.2 | 17-Jan-2022
##################################################
Add discount calculationType, discountAmount in store_discount_product table

#DB Changes:
ALTER TABLE store_discount_product ADD calculationType ENUM ('PERCENT','FIX');
ALTER TABLE store_discount_product ADD discountAmount DECIMAL(10,2);

Modify mysql function : fnGetItemDiscount()
Modify mysql stored procedure : getItemDiscount()


##################################################
# product-service-3.4.1 | 13-Jan-2022
##################################################
Bug fix for add product into discount
Add CustomRepository class to access entity manager refresh function to allow non-cache query


##################################################
# product-service-3.4.0 | 10-Jan-2022
##################################################
Add new field : displaySequence in store_category to allow category to be sorted by specific sequence
New function deleteStoreBannerMobileById
Bug fix for POST banner mobile

#DB Changes:
ALTER TABLE store_category ADD displaySequence INT;


##################################################
# product-service-3.3.9 | 7-Jan-2022
##################################################
Bug fix for update store details
Bug fix for discount product
Add new function for state delivery charges :  
1. postBulkStoreDeliveryCharge() 
2. deleteAllStateDeliveryCharge()


##################################################
# product-service-3.3.8 | 5-Jan-2022
##################################################

Add product details & category details in storeDiscountProduct


##################################################
# product-service-3.3.7 | 30-December-2021
##################################################

Add new function putStoreProductVariantsById & putStoreProductVariantAvailableById
Allow search by multiple discountType in searchDiscountByStoreId
Bug fix for create cost center code after creating store
Allow search by multiple verticalCode in getStore

#DB Changes:
Insert new permission role, only execute after product-service patched :
INSERT INTO role_authority VALUES ('STORE_OWNER','store-product-variants-put-by-id','product-service');
INSERT INTO role_authority VALUES ('STORE_OWNER','store-product-variant-available-put-by-id','product-service');

#New config:
deliveryService.createcentercode.URL=https://api.symplified.it/delivery-service/v1/deliveryEvent/createCentreCode/<storeId>


##################################################
# product-service-3.3.6 | 29-December-2021
##################################################

Bug fix for store timings, if breakStartTime & breakEndTime=null, then update null in db


##################################################
# product-service-3.3.5 | 28-December-2021
##################################################

1. Send request to delivery-service to create center code for Pakistan Store (with regionCountryId="PAK")
2. Bug fix for search store by domain : match exact domain -> disabled, waiting for new SF to be deploy
3. New function to update product asset : PUT /stores/{storeId}/products/{productId}/assets/update
4. Load item discount using mysql stored procedure

##DB Changes

-------------------------------------
ALTER TABLE `store` ADD costCenterCode VARCHAR(100);

-------------------------------------
DELIMITER $$

USE `symplified`$$

DROP PROCEDURE IF EXISTS `getItemDiscount`$$

CREATE PROCEDURE `getItemDiscount`(IN searchItemCode VARCHAR(50), IN searchStoreId VARCHAR(50))
BEGIN
	
	DECLARE isDone INT DEFAULT 0;
	DECLARE discountId VARCHAR(50);
	DECLARE foundDiscount VARCHAR(50);
	DECLARE checkDiscount VARCHAR(50);
	
	DECLARE cur1 CURSOR FOR SELECT id FROM store_discount WHERE storeId=searchStoreId AND isActive=1 AND startDate<NOW() AND endDate>NOW() AND discountType='ITEM';
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET  isDone=1;

	OPEN cur1; 
	
	read_loop: LOOP
		   FETCH cur1 INTO discountId;
		   SET checkDiscount = fnGetItemDiscount(searchItemCode, discountId);
		   IF checkDiscount <> 'NOTFOUND' THEN
			SET foundDiscount = checkDiscount;
		   END IF;
		   IF isDone = 1 THEN 
			LEAVE read_loop;
		   END IF;
	      END LOOP;
	CLOSE cur1;
	
	IF (foundDiscount IS NOT NULL) THEN
		SELECT discountName, startDate, endDate, normalPriceItemOnly, discountAmount, calculationType, storeDiscountId  
		FROM store_discount A INNER JOIN store_discount_tier B ON A.id=B.storeDiscountId WHERE A.id=foundDiscount;
	ELSE
		SELECT "NOTFOUND";
	END IF;
	
	END$$

DELIMITER ;

-------------------------------------
DELIMITER $$

USE `symplified`$$

DROP FUNCTION IF EXISTS `fnGetItemDiscount`$$

CREATE FUNCTION `fnGetItemDiscount`(searchItemCode VARCHAR(50), searchDiscountId VARCHAR(50) ) RETURNS VARCHAR(50) CHARSET utf8mb4
    DETERMINISTIC
BEGIN
    
    DECLARE foundItemId VARCHAR(50);
    DECLARE foundProductId VARCHAR(50);
    DECLARE foundCategoryId VARCHAR(50); 
    DECLARE foundDiscount VARCHAR(50);
    DECLARE foundSubItemId VARCHAR(50);
    
    SET foundDiscount = "NOTFOUND";
            
			#get item inside the product
			SELECT id INTO foundItemId FROM store_discount_product WHERE itemCode=searchItemCode AND storeDiscountId=searchDiscountId;
			#return concat("test",itemId);
			
			IF foundItemId IS NOT NULL THEN
				#RETURN searchDiscountId;
				SET foundDiscount = searchDiscountId;
			ELSE
				#get item inside categoryId
				SELECT productId, categoryId INTO foundProductId, foundCategoryId FROM product_inventory A 
					INNER JOIN product B ON A.productId=B.id WHERE itemCode=searchItemCode;
					
				IF (foundCategoryId IS NOT NULL) THEN
					SELECT id INTO foundSubItemId FROM store_discount_product WHERE categoryId=foundCategoryId AND storeDiscountId=searchDiscountId;
					
					IF foundSubItemId IS NOT NULL THEN
						#RETURN searchDiscountId;
						SET foundDiscount = searchDiscountId;
					END IF;
				END IF;
			END IF;
			RETURN foundDiscount;
    END$$

DELIMITER ;
---------------------------


##################################################
# product-service-3.3.4 | 23-December-2021
##################################################

Bug fix for search store discount :
-search discount by %discountName%
-add order by, order column in request parameter

Bug fix for store category :
-add order by, order column in request parameter


##################################################
# product-service-3.3.3 | 15-December-2021
##################################################
Save qr code in db during create store & update store , instead of generate on the fly

##DB Changes
ALTER TABLE `store_asset` ADD qrCodeUrl VARCHAR(300);
 

##################################################
# product-service-3.3.2 | 13-December-2021
##################################################
Add discount id in product discount details
Add discount details in response of getStoreProductInventorysById


##################################################
# product-service-3.3.1 | 10-December-2021
##################################################
Add maxDiscountAmount in request & response store discount controller
Add new function to view QR code for store url 


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
	
	INSERT INTO `order_completion_status_config` SELECT id,'E-Commerece',STATUS,storePickup,storeDeliveryType,
	paymentType,
	statusSequence, emailToCustomer, emailToStore, requestDelivery, rcMessage, 
	`pushNotificationToMerchat`, customerEmailContent, `storeEmailContent`,
	`rcMessageContent`, `comments`, `created`, `updated`, `storePushNotificationContent`, `storePushNotificationTitle`,
	`nextActionText`,
	`emailToFinance`, `financeEmailContent`
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