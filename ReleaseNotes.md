##################################################
# product-service-3.10.36| 20-October-2022 
##################################################
1. Code refactoring product add on template

##################################################
# product-service-3.10.35| 14-October-2022 
##################################################
1. Fix product that has price 0 not shwowing according to platform type
2. chnage a bit for the query for findng status not deletd
3. fix bug when create product with chinese character by removing generate seoname and seorul from backend
##################################################
# product-service-3.10.34| 14-October-2022 
##################################################
1. Update the statu of product add on instead of delete
##################################################
# product-service-3.10.33| 06-October-2022 
##################################################
1. Product Add on post with bulk
##################################################
# product-service-3.10.32| 06-October-2022 
##################################################
1. Product Add On features

##DB Changes

CREATE TABLE `addon_template_group` (
  `id` varchar(50) NOT NULL,
  `storeId` varchar(50) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `addon_template_item` (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `groupId` varchar(50) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `dineInPrice` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- symplified.product_addon definition

-- symplified.product_addon definition

CREATE TABLE `product_addon` (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `productId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `addonTemplateItemId` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `dineInPrice` decimal(10,2) DEFAULT NULL,
  `status` enum('AVAILABLE','NOTAVAILABLE','OUTOFSTOCK') DEFAULT NULL,
  `sequenceNumber` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `productId` (`productId`),
  KEY `addOnItemId` (`addonTemplateItemId`),
  CONSTRAINT `product_addon_ibfk_1` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `product_addon_ibfk_2` FOREIGN KEY (`addonTemplateItemId`) REFERENCES `addon_template_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- symplified.product_addon_group definition

CREATE TABLE `product_addon_group` (
  `id` varchar(50) NOT NULL,
  `addonTemplateGroupId` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `minAllowed` int DEFAULT NULL,
  `maxAllowed` int DEFAULT NULL,
  `sequenceNumber` int DEFAULT '0',
  `productId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_addon_group_FK` (`addonTemplateGroupId`),
  CONSTRAINT `product_addon_group_FK` FOREIGN KEY (`addonTemplateGroupId`) REFERENCES `addon_template_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
##################################################
# product-service-3.10.31| 06-October-2022 
##################################################
1. Sort the list of productInventories in with ascending of price
##################################################
# product-service-3.10.30| 23-September-2022 
##################################################
1. bulk delete product and categories
##################################################
# product-service-3.10.29| 22-September-2022 
##################################################

1. Hide product that has price 0
2. Add new column for product_package_option, product_package_option_detail 
3. Nested sort the sequence number

##DB Changes
ALTER TABLE symplified.product_package_option ADD sequenceNumber int DEFAULT 0;
ALTER TABLE symplified.product_package_option_detail ADD isDefault tinyint(1) DEFAULT 0;
ALTER TABLE symplified.product_package_option_detail ADD sequenceNumber int DEFAULT 0;

##################################################
# product-service-3.10.28| 09-September-2022 
##################################################
1. AUTO CALCULATE PRICE FOR DINE IN OR DELIVERY

##################################################
# product-service-3.10.27| 09-September-2022 
##################################################

##DB Changes
ALTER TABLE symplified.store ADD isDineIn tinyint(1) DEFAULT 0 NULL;
ALTER TABLE symplified.store ADD dineInOption enum('SELFCOLLECT','SENDTOTABLE') DEFAULT 'SELFCOLLECT' NULL;
ALTER TABLE symplified.store ADD dineInPaymentType enum('COD') DEFAULT 'COD' NULL;
ALTER TABLE symplified.store ADD isDelivery tinyint(1) DEFAULT 1 NULL;

ALTER TABLE symplified.product_inventory ADD dineInPrice decimal(10,2) NULL;

** Taufik did the query for auto calculate dine in price for existing database

##################################################
# product-service-3.10.26| 07-September-2022 
##################################################
1. File handling if not exists
##################################################
# product-service-3.10.25 | 07-September-2022 
##################################################
1. Generate sitemap based on location of marketplace

##Application properties Changes:
 
path.main.sitemapxml =
      /home/docker/Software/assets/frontend-assets/sitemap.xml
	  
##################################################
# product-service-3.10.24 | 02-September-2022 
##################################################
1. Fix bug for sort product price by Adding predicate builder for sort
##################################################
# product-service-3.10.23 | 30-August-2022 
##################################################
1. change query store product get controller and add new query param
##################################################
# product-service-3.10.22 | 26-August-2022 
##################################################
1. Change query for before cloning product

##################################################
# product-service-3.10.21 | 16-August-2022 
##################################################
1. Duplicate Bulk Products with Variant / Combo from Other Merchant Acc
2. Add new enum 'CoverImageUrl' storeAssets

##DB Changes:
 ALTER TABLE symplified.store_assets MODIFY COLUMN assetType enum('CoverImageUrl','BannerDesktopUrl','BannerMobileUrl','FaviconUrl','DiscountBannerUrl','QrcodeUrl','LogoUrl') CHARACTER SET utf8 COLLATE utf8_general_ci NULL;

##################################################
# product-service-3.10.20 | 11-August-2022 
##################################################
1. Merge hotfix 
##################################################
# product-service-3.10.19 | 11-August-2022 
##################################################

1. Fix whatsapp issue
##################################################
# product-service-3.10.18 | 10-August-2022 
##################################################

1. Search products and filter image not null , post product assets with url

##################################################
# product-service-3.10.17 | 09-August-2022 
##################################################

1. Create new endpoint get "/marketplace-popup"

##DB Changes:

CREATE TABLE `marketplace_popup_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `popupUrl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `regionCountryId` varchar(3) DEFAULT NULL,
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DESKTOP or MOBILE',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  `actionUrl` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `marketplace_banner_config_FK` (`regionCountryId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
##################################################
# product-service-3.10.16 | 01-August-2022 
##################################################

1. Add new response colum actionUrl in marketplace banner 
##################################################
# product-service-3.10.15 | 27-July-2022 
##################################################

1. Generate seoname and show marketplace url

##################################################
# product-service-3.10.14 | 25-July-2022 
##################################################

1. Bulk inventory and inventory items

##################################################
# product-service-3.10.13 | 19-July-2022 
##################################################

1. Marketplace banner config with sequence
##################################################
# product-service-3.10.12 | 19-July-2022 
##################################################

1. Include store category data in product with details
##################################################
# product-service-3.10.11 | 18-July-2022 
##################################################

1. Create new endpoint for postStoreTiming bulk (Support for create data and update data with bulk)

##################################################
# product-service-3.10.10 | 15-July-2022 
##################################################
1. Create endpoint for platform-og-tag

INSERT INTO `authority` (`id`, `serviceId`, `name`, `description`) VALUES
('platform-og-tag-get', 'product-service', 'getPlatformOgTag', '{GET /platform-og-tag, produces [application/json]}');

INSERT INTO `role_authority` (`roleId`, `authorityId`, `serviceId`) VALUES
('STORE_OWNER', 'platform-og-tag-get', 'product-service'),
('SUPER_USER', 'platform-og-tag-get', 'product-service');

##################################################
# product-service-3.10.9 | 7-July-2022 
##################################################
1. Rebuild
2. update table and insert data of platform logo square in platform_config

##DB Changes:
ALTER TABLE platform_config ADD platformLogoSquare varchar(500) NULL;


UPDATE platform_config 
SET platformLogoSquare = '/store-assets/DeliverIn-Logo_300X300.png'
WHERE platformName = 'Deliverin'

##################################################
# product-service-3.10.8 | 6-July-2022 
##################################################
1. Rebuild
##################################################
# product-service-3.10.7 | 5-July-2022 
##################################################
1. Handling null value for asset url
##################################################
# product-service-3.10.6 | 5-July-2022 
##################################################
1. set url service for image url 
2. Change config
3. 

##Config changes 
asset.service.url = https://assets.symplified.it //inject production asset service url

##DB Changes:

	UPDATE product 
	SET thumbnailUrl = REPLACE(thumbnailUrl, 'https://symplified.it', '') 
	WHERE thumbnailUrl LIKE '%https://symplified.it%';

	UPDATE product 
	SET thumbnailUrl = REPLACE(thumbnailUrl, 'https://symplified.biz', '') 
	WHERE thumbnailUrl  LIKE '%https://symplified.biz%';

	UPDATE product_asset
	SET url = REPLACE(url, 'https://symplified.it', '') 
	WHERE url LIKE '%https://symplified.it%';

	UPDATE product_asset
	SET url = REPLACE(url, 'https://symplified.biz', '') 
	WHERE url LIKE '%https://symplified.biz%';

	UPDATE store_assets
	SET assetUrl = REPLACE(assetUrl, 'https://symplified.it', '') 
	WHERE assetUrl LIKE '%https://symplified.it%';

	UPDATE store_assets
	SET assetUrl = REPLACE(assetUrl, 'https://symplified.biz', '') 
	WHERE assetUrl LIKE '%https://symplified.biz%';

	UPDATE store_category
	SET thumbnailUrl = REPLACE(thumbnailUrl, 'https://symplified.it', '') 
	WHERE thumbnailUrl LIKE '%https://symplified.it%';

	UPDATE store_category
	SET thumbnailUrl = REPLACE(thumbnailUrl, 'https://symplified.biz', '') 
	WHERE thumbnailUrl LIKE '%https://symplified.biz%';

##################################################
# product-service-3.10.5 | 1-July-2022 
##################################################
1. Add isSnooze for /stores endpoint

##################################################
# product-service-3.10.4 | 30-June-2022 
##################################################
1. Market place banner set url service for images url

##DB Changes:

	UPDATE marketplace_banner_config 
	SET bannerUrl = REPLACE(bannerUrl, 'https://symplified.it', '') 
	WHERE bannerUrl  LIKE '%https://symplified.it%';

##################################################
# product-service-3.10.3 | 30-June-2022 
##################################################
1. Plaform service set url service for images url
2. Update db in platform config

##DB Changes:

	UPDATE platform_config 
	SET platformLogo = REPLACE(platformLogo, 'https://symplified.it', '') 
	WHERE platformLogo  LIKE '%https://symplified.it%';

	UPDATE platform_config 
	SET platformLogoDark = REPLACE(platformLogoDark, 'https://symplified.it', '') 
	WHERE platformLogoDark  LIKE '%https://symplified.it%';

	UPDATE platform_config 
	SET platformFavIcon = REPLACE(platformFavIcon, 'https://symplified.it', '') 
	WHERE platformFavIcon  LIKE '%https://symplified.it%';

	UPDATE platform_config 
	SET platformFavIcon32 = REPLACE(platformFavIcon32, 'https://symplified.it', '') 
	WHERE platformFavIcon32  LIKE '%https://symplified.it%'

##################################################
# product-service-3.10.2 | 30-June-2022 
##################################################
1. Remove code for version 3.8.0 since it is already implemented in location service

##################################################
# product-service-3.10.1 | 27-June-2022 
##################################################
1. add request param type('DESKTOP'|'MOBILE') for banner-config endpoint 

##################################################
# product-service-3.10.0 | 23-June-2022
##################################################

1. Check domain name of store with reservde domain name
2. DB Changes : Create new table 'reserved_domain'


##DB Changes:

 CREATE TABLE symplified.reserved_domain (
 	id bigint auto_increment NOT NULL,
 	`domain` varchar(300) NULL,
 	CONSTRAINT reserved_domain_PK PRIMARY KEY (id)
 );

INSERT INTO symplified.reserved_domain (`domain`) VALUES('payment');
INSERT INTO symplified.reserved_domain (`domain`) VALUES('admin');
INSERT INTO symplified.reserved_domain (`domain`) VALUES('customer');



##################################################
# product-service-3.9.0 | 10-June-2022
##################################################

1. Create new endpoint to get all marketplace banner image ( get :/banner-config?regionCountryId=:? )
2. DB Changes : Create new table for market place banner


##DB Changes:

CREATE TABLE `marketplace_banner_config` (
	id bigint auto_increment NOT NULL,
	bannerUrl varchar(500) NULL,
	regionCountryId varchar(3) NULL,
	CONSTRAINT marketplace_banner_config_PK PRIMARY KEY (id),
	CONSTRAINT marketplace_banner_config_FK FOREIGN KEY (regionCountryId) REFERENCES symplified.region_country(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);


##################################################
# product-service-3.8.0 | 21-May-2022
##################################################

1. Create new endpoint to get all products based on parentCategoryId get :/products/parent-category

# product-service-3.7.4 | 14-Jun-2022
##################################################

1. Fix seoUrl in store product
##################################################
# product-service-3.7.3 | 25-May-2022
##################################################

1. Chnage table for state city

##################################################
# product-service-3.7.2 | 20-May-2022
##################################################

1. Remove pageable body region-country-state-city-get

##################################################
# product-service-3.7.1 | 19-May-2022
##################################################

1. Alter table for promo text 
2. Add new request param

##DB Changes:

ALTER TABLE promo_text ADD 
verticaCode VARCHAR(50),
;

##################################################
# product-service-3.7.0 | 13-May-2022
##################################################
1. Alter table delivery_zone_city
2. Create new endpoint {GET /region-country-state-city, produces [application/json]}
3. Assign authority permission and role permission for the new endpoint 
4. Insert cities of Malaysia in table delivery_zone_city

##DB Changes:

ALTER TABLE delivery_zone_city ADD 
country VARCHAR(100),
state VARCHAR(100)
;

INSERT INTO `authority` (`id`, `serviceId`, `name`, `description`) VALUES
('region-country-state-city-get', 'product-service', 'getRegionCountryStateCity', '{GET /region-country-state-city, produces [application/json]}')
;

INSERT INTO `role_authority` (`roleId`, `authorityId`, `serviceId`) VALUES
('STORE_OWNER', 'region-country-state-city-get', 'product-service'),
('SUPER_USER', 'region-country-state-city-get', 'product-service')
;

INSERT INTO `delivery_zone_city` (city,country, state) VALUES
('Ampang', 'MYS', 'Selangor'),
('Bandar Baru Bangi', 'MYS', 'Selangor'),
('Bandar Puncak Alam', 'MYS', 'Selangor'),
('Banting', 'MYS', 'Selangor'),
('Batang Kali', 'MYS', 'Selangor'),
('Batu Arang', 'MYS', 'Selangor'),
('Batu Caves', 'MYS', 'Selangor'),
('Beranang', 'MYS', 'Selangor'),
('Bestari Jaya', 'MYS', 'Selangor'),
('Bukit Rotan', 'MYS', 'Selangor'),
('Cheras', 'MYS', 'Selangor'),
('Cyberjaya', 'MYS', 'Selangor'),
('Dengkil', 'MYS', 'Selangor'),
('Hulu Langat', 'MYS', 'Selangor'),
('Jenjarom', 'MYS', 'Selangor'),
('Jeram', 'MYS', 'Selangor'),
('Kajang', 'MYS', 'Selangor'),
('Kapar', 'MYS', 'Selangor'),
('Kerling', 'MYS', 'Selangor'),
('Klang', 'MYS', 'Selangor'),
('KLIA', 'MYS', 'Selangor'),
('Kuala Kubu Baru', 'MYS', 'Selangor'),
('Kuala Selangor', 'MYS', 'Selangor'),
('Kuang', 'MYS', 'Selangor'),
('Pelabuhan Klang', 'MYS', 'Selangor'),
('Petaling Jaya', 'MYS', 'Selangor'),
('Puchong', 'MYS', 'Selangor'),
('Pulau Indah', 'MYS', 'Selangor'),
('Pulau Carey', 'MYS', 'Selangor'),
('Pulau Ketam', 'MYS', 'Selangor'),
('Rasa', 'MYS', 'Selangor'),
('Rawang', 'MYS', 'Selangor'),
('Sabak Bernam', 'MYS', 'Selangor'),
('Sekinchan', 'MYS', 'Selangor'),
('Semenyih', 'MYS', 'Selangor'),
('Sepang', 'MYS', 'Selangor'),
('Serdang', 'MYS', 'Selangor'),
('Serendah', 'MYS', 'Selangor'),
('Seri Kembangan', 'MYS', 'Selangor'),
('Sungai Ayer Tawar', 'MYS', 'Selangor'),
('Sungai Besar', 'MYS', 'Selangor'),
('Sungai Buloh', 'MYS', 'Selangor'),
('Sungai Pelek', 'MYS', 'Selangor'),
('Tanjong Karang', 'MYS', 'Selangor'),
('Tanjong Sepat', 'MYS', 'Selangor'),
('Telok Panglima Garang', 'MYS', 'Selangor');

INSERT INTO `delivery_zone_city` (city,country, state) VALUES
('Alor Gajah', 'MYS', 'Melaka'),
('Asahan', 'MYS', 'Melaka'),
('Ayer Keroh', 'MYS', 'Melaka'),
('Bemban', 'MYS', 'Melaka'),
('Durian Tunggal', 'MYS', 'Melaka'),
('Jasin', 'MYS', 'Melaka'),
('Kem Terendak', 'MYS', 'Melaka'),
('Kuala Sungai Baru', 'MYS', 'Melaka'),
('Lubok China', 'MYS', 'Melaka'),
('Masjid Tanah', 'MYS', 'Melaka'),
('Melaka', 'MYS', 'Melaka'),
('Merlimau', 'MYS', 'Melaka'),
('Selandar', 'MYS', 'Melaka'),
('Sungai Rambai', 'MYS', 'Melaka'),
('Sungai Udang', 'MYS', 'Melaka'),
('Tanjong Kling', 'MYS', 'Melaka');

INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Ayer Baloi', 'MYS', 'Johor'),
('Ayer Hitam', 'MYS', 'Johor'),
('Ayer Tawar 2', 'MYS', 'Johor'),
('Bandar Penawar', 'MYS', 'Johor'),
('Bandar Tenggara', 'MYS', 'Johor'),
('Batu Anam', 'MYS', 'Johor'),
('Batu Pahat', 'MYS', 'Johor'),
('Bekok', 'MYS', 'Johor'),
('Benut', 'MYS', 'Johor'),
('Bukit Gambir', 'MYS', 'Johor'),
('Bukit Pasir', 'MYS', 'Johor'),
('Chaah', 'MYS', 'Johor'),
('Endau', 'MYS', 'Johor'),
('Gelang Patah', 'MYS', 'Johor'),
('Gerisek', 'MYS', 'Johor'),
('Gugusan Taib Andak', 'MYS', 'Johor'),
('Iskandar Puteri', 'MYS', 'Johor'),
('Jementah', 'MYS', 'Johor'),
('Johor Bahru', 'MYS', 'Johor'),
('Kahang', 'MYS', 'Johor'),
('Kluang', 'MYS', 'Johor'),
('Kota Tinggi', 'MYS', 'Johor'),
('Kukup', 'MYS', 'Johor'),
('Kulai', 'MYS', 'Johor'),
('Labis', 'MYS', 'Johor'),
('Layang-Layang', 'MYS', 'Johor'),
('Masai', 'MYS', 'Johor'),
('Mersing', 'MYS', 'Johor'),
('Muar', 'MYS', 'Johor'),
('Pagoh', 'MYS', 'Johor'),
('Paloh', 'MYS', 'Johor'),
('Panchor', 'MYS', 'Johor'),
('Parit Jawa', 'MYS', 'Johor'),
('Parit Raja', 'MYS', 'Johor'),
('Parit Sulong', 'MYS', 'Johor'),
('Pasir Gudang', 'MYS', 'Johor'),
('Pekan Nenas', 'MYS', 'Johor'),
('Pengerang', 'MYS', 'Johor'),
('Pontian', 'MYS', 'Johor'),
('Pulau Satu', 'MYS', 'Johor'),
('Rengam', 'MYS', 'Johor'),
('Rengit', 'MYS', 'Johor'),
('Segamat', 'MYS', 'Johor'),
('Semerah', 'MYS', 'Johor'),
('Senai', 'MYS', 'Johor'),
('Senggarang', 'MYS', 'Johor'),
('Seri Gading', 'MYS', 'Johor'),
('Seri Medan', 'MYS', 'Johor'),
('Simpang Rengam', 'MYS', 'Johor'),
('Sungai Mati', 'MYS', 'Johor'),
('Tangkak', 'MYS', 'Johor'),
('Ulu Tiram', 'MYS', 'Johor'),
('Yong Peng', 'MYS', 'Johor');

INSERT INTO `delivery_zone_city` (city, country, state)VALUES
('Alor Setar', 'MYS', 'Kedah'),
('Ayer Hitam', 'MYS', 'Kedah'),
('Baling', 'MYS', 'Kedah'),
('Bandar Baharu', 'MYS', 'Kedah'),
('Bedong', 'MYS', 'Kedah'),
('Bukit Kayu Hitam', 'MYS', 'Kedah'),
('Changloon', 'MYS', 'Kedah'),
('Gurun', 'MYS', 'Kedah'),
('Jeniang', 'MYS', 'Kedah'),
('Jitra', 'MYS', 'Kedah'),
('Karangan', 'MYS', 'Kedah'),
('Kepala Batas', 'MYS', 'Kedah'),
('Kodiang', 'MYS', 'Kedah'),
('Kota Kuala Muda', 'MYS', 'Kedah'),
('Kota Sarang Semut', 'MYS', 'Kedah'),
('Kuala Kedah', 'MYS', 'Kedah'),
('Kuala Ketil', 'MYS', 'Kedah'),
('Kuala Nerang', 'MYS', 'Kedah'),
('Kuala Pegang', 'MYS', 'Kedah'),
('Kulim', 'MYS', 'Kedah'),
('Kupang', 'MYS', 'Kedah'),
('Langgar', 'MYS', 'Kedah'),
('Langkawi', 'MYS', 'Kedah'),
('Lunas', 'MYS', 'Kedah'),
('Merbok', 'MYS', 'Kedah'),
('Padang Serai', 'MYS', 'Kedah'),
('Pendang', 'MYS', 'Kedah'),
('Pokok Sena', 'MYS', 'Kedah'),
('Serdang', 'MYS', 'Kedah'),
('Sik', 'MYS', 'Kedah'),
('Simpang Empat', 'MYS', 'Kedah'),
('Sungai Petani', 'MYS', 'Kedah'),
('Universiti Utara Malaysia', 'MYS', 'Kedah'),
('Yan', 'MYS', 'Kedah');


INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Kota Bharu', 'MYS', 'Kelantan'),
('Bachok', 'MYS', 'Kelantan'),
('Wakaf Bharu', 'MYS', 'Kelantan'),
('Tumpat', 'MYS', 'Kelantan'),
('Melor', 'MYS', 'Kelantan'),
('Ketereh', 'MYS', 'Kelantan'),
('Kem Desa Pahlawan', 'MYS', 'Kelantan'),
('Pulai Chondong', 'MYS', 'Kelantan'),
('Cherang Ruku', 'MYS', 'Kelantan'),
('Pasir Puteh', 'MYS', 'Kelantan'),
('Selising', 'MYS', 'Kelantan'),
('Pasir Mas', 'MYS', 'Kelantan'),
('Rantau Panjang', 'MYS', 'Kelantan'),
('Tanah Merah', 'MYS', 'Kelantan'),
('Jeli', 'MYS', 'Kelantan'),
('Kuala Balah', 'MYS', 'Kelantan'),
('Ayer Lanas', 'MYS', 'Kelantan'),
('Kuala Krai', 'MYS', 'Kelantan'),
('Dabong', 'MYS', 'Kelantan'),
('Gua Musang', 'MYS', 'Kelantan'),
('Temangan', 'MYS', 'Kelantan'),
('Machang', 'MYS', 'Kelantan');

INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Bahau', 'MYS', 'Negeri Sembilan'),
('Bandar Enstek', 'MYS', 'Negeri Sembilan'),
('Bandar Seri Jempol', 'MYS', 'Negeri Sembilan'),
('Batu Kikir', 'MYS', 'Negeri Sembilan'),
('Gemas', 'MYS', 'Negeri Sembilan'),
('Gemencheh', 'MYS', 'Negeri Sembilan'),
('Johol', 'MYS', 'Negeri Sembilan'),
('Kota', 'MYS', 'Negeri Sembilan'),
('Kuala Klawang', 'MYS', 'Negeri Sembilan'),
('Kuala Pilah', 'MYS', 'Negeri Sembilan'),
('Labu', 'MYS', 'Negeri Sembilan'),
('Linggi', 'MYS', 'Negeri Sembilan'),
('Mantin', 'MYS', 'Negeri Sembilan'),
('Niai', 'MYS', 'Negeri Sembilan'),
('Nilai', 'MYS', 'Negeri Sembilan'),
('Port Dickson', 'MYS', 'Negeri Sembilan'),
('Pusat Bandar Palong', 'MYS', 'Negeri Sembilan'),
('Rantau', 'MYS', 'Negeri Sembilan'),
('Rembau', 'MYS', 'Negeri Sembilan'),
('Rompin', 'MYS', 'Negeri Sembilan'),
('Seremban', 'MYS', 'Negeri Sembilan'),
('Si Rusa', 'MYS', 'Negeri Sembilan'),
('Simpang Durian', 'MYS', 'Negeri Sembilan'),
('Simpang Pertang', 'MYS', 'Negeri Sembilan'),
('Tampin', 'MYS', 'Negeri Sembilan'),
('Tanjong Ipoh', 'MYS', 'Negeri Sembilan');

 INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Balok', 'MYS', 'Pahang'),
('Bandar Bera', 'MYS', 'Pahang'),
('Bandar Pusat Jengka', 'MYS', 'Pahang'),
('Bandar Tun Abdul Razak', 'MYS', 'Pahang'),
('Benta', 'MYS', 'Pahang'),
('Bentong', 'MYS', 'Pahang'),
('Brinchang', 'MYS', 'Pahang'),
('Bukit Fraser', 'MYS', 'Pahang'),
('Bukit Goh', 'MYS', 'Pahang'),
('Bukit Kuin', 'MYS', 'Pahang'),
('Chenor', 'MYS', 'Pahang'),
('Chini', 'MYS', 'Pahang'),
('Damak', 'MYS', 'Pahang'),
('Dong', 'MYS', 'Pahang'),
('Gambang', 'MYS', 'Pahang'),
('Genting Highlands', 'MYS', 'Pahang'),
('Jerantut', 'MYS', 'Pahang'),
('Karak', 'MYS', 'Pahang'),
('Kemayan', 'MYS', 'Pahang'),
('Kuala Krau', 'MYS', 'Pahang'),
('Kuala Lipis', 'MYS', 'Pahang'),
('Kuala Rompin', 'MYS', 'Pahang'),
('Kuantan', 'MYS', 'Pahang'),
('Lanchang', 'MYS', 'Pahang'),
('Lurah Bilut', 'MYS', 'Pahang'),
('Maran', 'MYS', 'Pahang'),
('Mentakab', 'MYS', 'Pahang'),
('Muadzam Shah', 'MYS', 'Pahang'),
('Padang Tengku', 'MYS', 'Pahang'),
('Pekan', 'MYS', 'Pahang'),
('Raub', 'MYS', 'Pahang'),
('Ringlet', 'MYS', 'Pahang'),
('Sega', 'MYS', 'Pahang'),
('Sungai Koyan', 'MYS', 'Pahang'),
('Sungai Lembing', 'MYS', 'Pahang'),
('Tanah Rata', 'MYS', 'Pahang'),
('Temerloh', 'MYS', 'Pahang'),
('Triang', 'MYS', 'Pahang');

INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Arau', 'MYS', 'Perlis'),
('Kaki Bukit', 'MYS', 'Perlis'),
('Kangar', 'MYS', 'Perlis'),
('Kuala Perlis', 'MYS', 'Perlis'),
('Padang Besar', 'MYS', 'Perlis'),
('Simpang Ampat', 'MYS', 'Perlis');

INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Ayer Itam', 'MYS','Penang'),
('Balik Pulau', 'MYS','Penang'),
('Batu Ferringhi', 'MYS','Penang'),
('Batu Maung', 'MYS','Penang'),
('Bayan Lepas', 'MYS','Penang'),
('Bukit Mertajam', 'MYS','Penang'),
('Butterworth', 'MYS','Penang'),
('Gelugor', 'MYS','Penang'),
('Jelutong', 'MYS','Penang'),
('Kepala Batas', 'MYS','Penang'),
('Kubang Semang', 'MYS','Penang'),
('Nibong Tebal', 'MYS','Penang'),
('Penaga', 'MYS','Penang'),
('Penang Hill', 'MYS','Penang'),
('Perai', 'MYS','Penang'),
('Permatang Pauh', 'MYS','Penang'),
('Pulau Pinang', 'MYS','Penang'),
('Simpang Ampat', 'MYS','Penang'),
('Sungai Jawi', 'MYS','Penang'),
('Tanjung Bungah', 'MYS','Penang'),
('Tasek Gelugor', 'MYS','Penang'),
('USM Pulau Pinang', 'MYS','Penang');

 INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Ayer Tawar', 'MYS', 'Perak'),
('Bagan Datoh', 'MYS', 'Perak'),
('Bagan Serai', 'MYS', 'Perak'),
('Bandar Seri Iskandar', 'MYS', 'Perak'),
('Batu Gajah', 'MYS', 'Perak'),
('Batu Kurau', 'MYS', 'Perak'),
('Behrang Stesen', 'MYS', 'Perak'),
('Bidor', 'MYS', 'Perak'),
('Bota', 'MYS', 'Perak'),
('Bruas', 'MYS', 'Perak'),
('Changkat Jering', 'MYS', 'Perak'),
('Changkat Keruing', 'MYS', 'Perak'),
('Chemor', 'MYS', 'Perak'),
('Chenderiang', 'MYS', 'Perak'),
('Chenderong Balai', 'MYS', 'Perak'),
('Chikus', 'MYS', 'Perak'),
('Enggor', 'MYS', 'Perak'),
('Gerik', 'MYS', 'Perak'),
('Gopeng', 'MYS', 'Perak'),
('Hutan Melintang', 'MYS', 'Perak'),
('Intan', 'MYS', 'Perak'),
('Ipoh', 'MYS', 'Perak'),
('Jeram', 'MYS', 'Perak'),
('Kampar', 'MYS', 'Perak'),
('Kampung Gajah', 'MYS', 'Perak'),
('Kampung Kepayang', 'MYS', 'Perak'),
('Kamunting', 'MYS', 'Perak'),
('Kuala Kangsar', 'MYS', 'Perak'),
('Kuala Kurau', 'MYS', 'Perak'),
('Kuala Sepetang', 'MYS', 'Perak'),
('Lambor Kanan', 'MYS', 'Perak'),
('Langkap', 'MYS', 'Perak'),
('Lenggong', 'MYS', 'Perak'),
('Lumut', 'MYS', 'Perak'),
('Malim Nawar', 'MYS', 'Perak'),
('Manong', 'MYS', 'Perak'),
('Matang', 'MYS', 'Perak'),
('Padang Rengas', 'MYS', 'Perak'),
('Pangkor', 'MYS', 'Perak'),
('Pantai Remis', 'MYS', 'Perak'),
('Parit', 'MYS', 'Perak'),
('Parit Buntar', 'MYS', 'Perak'),
('Pengkalan Hulu', 'MYS', 'Perak'),
('Pusing', 'MYS', 'Perak'),
('Rantau Panjang', 'MYS', 'Perak'),
('Sauk', 'MYS', 'Perak'),
('Selama', 'MYS', 'Perak'),
('Selekoh', 'MYS', 'Perak'),
('Seri Manjung', 'MYS', 'Perak'),
('Simpang', 'MYS', 'Perak'),
('Simpang Ampat Semanggol', 'MYS', 'Perak'),
('Sitiawan', 'MYS', 'Perak'),
('Slim River', 'MYS', 'Perak'),
('Sungai Siput', 'MYS', 'Perak'),
('Sungai Sumun', 'MYS', 'Perak'),
('Sungkai', 'MYS', 'Perak'),
('Taiping', 'MYS', 'Perak'),
('Tanjong Malim', 'MYS', 'Perak'),
('Tanjong Piandang', 'MYS', 'Perak'),
('Tanjong Rambutan', 'MYS', 'Perak'),
('Tanjong Tualang', 'MYS', 'Perak'),
('Tapah', 'MYS', 'Perak'),
('Tapah Road', 'MYS', 'Perak'),
('Teluk Intan', 'MYS', 'Perak'),
('Temoh', 'MYS', 'Perak'),
('TLDM Lumut', 'MYS', 'Perak'),
('Trolak', 'MYS', 'Perak'),
('Trong', 'MYS', 'Perak'),
('Tronoh', 'MYS', 'Perak'),
('Ulu Bernam', 'MYS', 'Perak'),
('Ulu Kinta', 'MYS', 'Perak');

INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Beaufort', 'MYS', 'Sabah'),
('Beluran', 'MYS', 'Sabah'),
('Beverly', 'MYS', 'Sabah'),
('Bongawan', 'MYS', 'Sabah'),
('Inanam', 'MYS', 'Sabah'),
('Keningau', 'MYS', 'Sabah'),
('Kota Belud', 'MYS', 'Sabah'),
('Kota Kinabalu', 'MYS', 'Sabah'),
('Kota Kinabatangan', 'MYS', 'Sabah'),
('Kota Marudu', 'MYS', 'Sabah'),
('Kuala Penyu', 'MYS', 'Sabah'),
('Kudat', 'MYS', 'Sabah'),
('Kunak', 'MYS', 'Sabah'),
('Lahad Datu', 'MYS', 'Sabah'),
('Likas', 'MYS', 'Sabah'),
('Membakut', 'MYS', 'Sabah'),
('Menumbok', 'MYS', 'Sabah'),
('Nabawan', 'MYS', 'Sabah'),
('Pamol', 'MYS', 'Sabah'),
('Papar', 'MYS', 'Sabah'),
('Penampang', 'MYS', 'Sabah'),
('Putatan', 'MYS', 'Sabah'),
('Ranau', 'MYS', 'Sabah'),
('Sandakan', 'MYS', 'Sabah'),
('Semporna', 'MYS', 'Sabah'),
('Sipitang', 'MYS', 'Sabah'),
('Tambunan', 'MYS', 'Sabah'),
('Tamparuli', 'MYS', 'Sabah'),
('Tanjung Aru', 'MYS', 'Sabah'),
('Tawau', 'MYS', 'Sabah'),
('Tenghilan', 'MYS', 'Sabah'),
('Tenom', 'MYS', 'Sabah'),
('Tuaran', 'MYS', 'Sabah');

INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Asajaya', 'MYS', 'Sarawak'),
('Balingian', 'MYS', 'Sarawak'),
('Baram', 'MYS', 'Sarawak'),
('Bau', 'MYS', 'Sarawak'),
('Bekenu', 'MYS', 'Sarawak'),
('Belaga', 'MYS', 'Sarawak'),
('Belawai', 'MYS', 'Sarawak'),
('Betong', 'MYS', 'Sarawak'),
('Bintangor', 'MYS', 'Sarawak'),
('Bintulu', 'MYS', 'Sarawak'),
('Dalat', 'MYS', 'Sarawak'),
('Daro', 'MYS', 'Sarawak'),
('Debak', 'MYS', 'Sarawak'),
('Engkilili', 'MYS', 'Sarawak'),
('Julau', 'MYS', 'Sarawak'),
('Kabong', 'MYS', 'Sarawak'),
('Kanowit', 'MYS', 'Sarawak'),
('Kapit', 'MYS', 'Sarawak'),
('Kota Samarahan', 'MYS', 'Sarawak'),
('Kuching', 'MYS', 'Sarawak'),
('Lawas', 'MYS', 'Sarawak'),
('Limbang', 'MYS', 'Sarawak'),
('Lingga', 'MYS', 'Sarawak'),
('Long Lama', 'MYS', 'Sarawak'),
('Lubok Antu', 'MYS', 'Sarawak'),
('Lundu', 'MYS', 'Sarawak'),
('Lutong', 'MYS', 'Sarawak'),
('Matu', 'MYS', 'Sarawak'),
('Miri', 'MYS', 'Sarawak'),
('Mukah', 'MYS', 'Sarawak'),
('Nanga Medamit', 'MYS', 'Sarawak'),
('Niah', 'MYS', 'Sarawak'),
('Pusa', 'MYS', 'Sarawak'),
('Roban', 'MYS', 'Sarawak'),
('Saratok', 'MYS', 'Sarawak'),
('Sarikei', 'MYS', 'Sarawak'),
('Sebauh', 'MYS', 'Sarawak'),
('Sebuyau', 'MYS', 'Sarawak'),
('Serian', 'MYS', 'Sarawak'),
('Sibu', 'MYS', 'Sarawak'),
('Siburan', 'MYS', 'Sarawak'),
('Simunjan', 'MYS', 'Sarawak'),
('Song', 'MYS', 'Sarawak'),
('Spaoh', 'MYS', 'Sarawak'),
('Sri Aman', 'MYS', 'Sarawak'),
('Sundar', 'MYS', 'Sarawak'),
('Tatau', 'MYS', 'Sarawak');

INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Ajil', 'MYS', 'Terengganu'),
('Al Muktatfi Billah Shah', 'MYS', 'Terengganu'),
('Ayer Puteh', 'MYS', 'Terengganu'),
('Bukit Besi', 'MYS', 'Terengganu'),
('Bukit Payong', 'MYS', 'Terengganu'),
('Ceneh', 'MYS', 'Terengganu'),
('Chalok', 'MYS', 'Terengganu'),
('Cukai', 'MYS', 'Terengganu'),
('Dungun', 'MYS', 'Terengganu'),
('Jerteh', 'MYS', 'Terengganu'),
('Kampung Raja', 'MYS', 'Terengganu'),
('Kemasek', 'MYS', 'Terengganu'),
('Kerteh', 'MYS', 'Terengganu'),
('Ketengah Jaya', 'MYS', 'Terengganu'),
('Kijal', 'MYS', 'Terengganu'),
('Kuala Berang', 'MYS', 'Terengganu'),
('Kuala Besut', 'MYS', 'Terengganu'),
('Kuala Terengganu', 'MYS', 'Terengganu'),
('Marang', 'MYS', 'Terengganu'),
('Paka', 'MYS', 'Terengganu'),
('Permaisuri', 'MYS', 'Terengganu'),
('Sungai Tong', 'MYS', 'Terengganu');

INSERT INTO `delivery_zone_city` (city, country, state) VALUES
('Kuala Lumpur', 'MYS', 'Kuala Lumpur'),
('Hulu Langat', 'MYS', 'Kuala Lumpur'),
('Labuan', 'MYS', 'Labuan'),
('Putrajaya', 'MYS', 'Putrajaya');

##################################################
# product-service-3.6.0 | 12-May-2022
##################################################

1. Add new column for store category
2. Insert data for top category based on vertical code
3. Create new endpoint {GET /promo-text, produces [application/json]} , {GET /promo-text/{eventId}, produces [application/json]}
4. Create new table for promo-text
5. add permission of the endpoint {GET /promo-text, produces [application/json]} , {GET /promo-text/{eventId}, produces [application/json]} into table authority
6. assign role permission into table role_authority


##DB Changes:

ALTER TABLE store_category ADD verticalCode VARCHAR(50);

INSERT INTO `store_category` (`id`,`name`, `verticalCode`) VALUES
('AutomotiveECommercePK','Automative','ECommerce_PK'),
('BabiesToysECommerce_PK','Babies & Toys','ECommerce_PK'),
('BeautyHealthCareECommercePK','Beauty & Health Care','ECommerce_PK'),
('ElectronicAccessoriesECommercePK','Electronic Accessories','ECommerce_PK'),
('ElectronicDevicesECommercePK','Electronic Devices','ECommerce_PK'),
('FashionAccessoriesECommercePK','Fashion Accessories','ECommerce_PK'),
('GamesHobbiesECommercePK','Games & Hobbies','ECommerce_PK'),
('GroceriesECommercePK','Groceries','ECommerce_PK'),
('HomeLivingECommercePK','Home & Living','ECommerce_PK'),
('HomeAppliancesECommercePK','Home Appliances','ECommerce_PK'),
('MenFashionECommercePK','Men Fashion','ECommerce_PK'),
('WomenFashionECommercePK','Women Fashion','ECommerce_PK'),
('OthersECommercePK','Others','ECommerce_PK'),
('PetsSuppliesECommercePK','Pets & Supplies','ECommerce_PK'),
('SportOutdoorECommercePK','Sport & Outdoor','ECommerce_PK')
;

INSERT INTO `store_category` (`id`,`name`, `verticalCode`) VALUES
('AutomotiveECommerce','Automative','E-Commerce'),
('BabiesToysECommerce','Babies & Toys','E-Commerce'),
('BeautyHealthCareECommerce','Beauty & Health Care','E-Commerce'),
('ElectronicAccessoriesECommerce','Electronic Accessories','E-Commerce'),
('ElectronicDevicesECommerce','Electronic Devices','E-Commerce'),
('FashionAccessoriesECommerce','Fashion Accessories','E-Commerce'),
('GamesHobbiesECommerce','Games & Hobbies','E-Commerce'),
('GroceriesECommerce','Groceries','E-Commerce'),
('HomeLivingECommerce','Home & Living','E-Commerce'),
('HomeAppliancesECommerce','Home Appliances','E-Commerce'),
('MenFashionECommerce','Men Fashion','E-Commerce'),
('WomenFashionECommerce','Women Fashion','E-Commerce'),
('OthersECommerce','Others','E-Commerce'),
('PetsSuppliesECommerce','Pets & Supplies','E-Commerce'),
('SportOutdoorECommerce','Sport & Outdoor','E-Commerce')
;

INSERT INTO `store_category` (`id`,`name`, `verticalCode`) VALUES
('BeveragesFnBPK','Beverages','FnB_PK'),
('ChineseCuisineFnBPK','Chinese Cuisine','FnB_PK'),
('DessertSnacksFnBPK','Dessert & Snacks','FnB_PK'),
('FastFoodFnBPK','Fast Food','FnB_PK'),
('IndianCuisineFnBPK','Indian Cuisine','FnB_PK'),
('IndonesianCuisineFnBPK','Indonesian Cuisine','FnB_PK'),
('JapaneseCuisineFnBPK','Japanese Cuisine','FnB_PK'),
('KoreanCuisineFnBPK','Korean Cuisine','FnB_PK'),
('MalayCuisineFnBPK','Malay Cuisine','FnB_PK'),
('ThailandCuisineFnBPK','Thailand Cuisine','FnB_PK'),
('VegetarianFnBPK','Vegetarian','FnB_PK'),
('WesternCuisineFnBPK','Western Cuisine','FnB_PK')
;

INSERT INTO `store_category` (`id`,`name`, `verticalCode`) VALUES
('BeveragesFnB','Beverages','FnB'),
('ChineseCuisineFnB','Chinese Cuisine','FnB'),
('DessertSnacksFnB','Dessert & Snacks','FnB'),
('FastFoodFnB','Fast Food','FnB'),
('IndianCuisineFnB','Indian Cuisine','FnB'),
('IndonesianCuisineFnB','Indonesian Cuisine','FnB'),
('JapaneseCuisineFnB','Japanese Cuisine','FnB'),
('KoreanCuisineFnB','Korean Cuisine','FnB'),
('MalayCuisineFnB','Malay Cuisine','FnB'),
('ThailandCuisineFnB','Thailand Cuisine','FnB'),
('VegetarianFnB','Vegetarian','FnB'),
('WesternCuisineFnB','Western Cuisine','FnB')
;

CREATE TABLE `promo_text` (
  `id` int NOT NULL AUTO_INCREMENT,
  `eventId` varchar(100) NOT NULL,
  `displayText` text,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `eventId` (`eventId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

INSERT INTO `authority` (`id`, `serviceId`, `name`, `description`) VALUES
('promo-text-get', 'product-service', 'getPromoText', '{GET /promo-text, produces [application/json]}'),
('promo-text-get-by-id', 'product-service', 'getPromoByEventId', '{GET /promo-text/{eventId}, produces [application/json]}')
;

INSERT INTO `role_authority` (`roleId`, `authorityId`, `serviceId`) VALUES
('STORE_OWNER', 'promo-text-get', 'product-service'),
('SUPER_USER', 'promo-text-get', 'product-service'),
('STORE_OWNER', 'promo-text-get-by-id', 'product-service'),
('SUPER_USER', 'promo-text-get-by-id', 'product-service')
;

##################################################
# product-service-3.5.22 | 14-Apr-2022
##################################################
Put sequence in region_country_state

##DB Changes:
ALTER TABLE region_country_state ADD sequence INT(1);


##################################################
# product-service-3.5.21 | 11-Apr-2022
##################################################
New field for platformConfig

##DB Changes:
ALTER TABLE platform_config ADD gaCode VARCHAR(50);
ALTER TABLE platform_config ADD platformFavIcon32 VARCHAR(255);


##################################################
# product-service-3.5.20 | 06-Apr-2022
##################################################
New API to retrieve data from platformConfig table

##DB Changes:
CREATE TABLE platform_config (
platformId VARCHAR(50) PRIMARY KEY,
platformName VARCHAR(255),
platformLogo VARCHAR(255),
platformLogoDark VARCHAR(255),
platformFavIcon VARCHAR(255),
platformType VARCHAR(50),
platformCountry VARCHAR(50),
domain VARCHAR(50)
);

need to copy data for this table from staging to production


##################################################
# product-service-3.5.19 | 06-Apr-2022
##################################################
Bug fix for store discount normalPriceItemOnly null in db


##################################################
# product-service-3.5.18 | 01-Apr-2022
##################################################
Bug fix for update product customNote 


##################################################
# product-service-3.5.17 | 31-Mar-2022
##################################################
ALTER TABLE product ADD isNoteOptional TINYINT(1) DEFAULT 1;
ALTER TABLE product ADD customNote VARCHAR(255);


##################################################
# product-service-3.5.16 | 24-Mar-2022
##################################################
Return success even cannot create live chat group during create new store


##################################################
# product-service-3.5.15 | 15-Mar-2022
##################################################
Bug fix for product asset file upload


##################################################
# product-service-3.5.14 | 14-Mar-2022
##################################################
Bug fix for product checkName


##################################################
# product-service-3.5.13 | 07-Mar-2022
##################################################
Bug fix for validateStoreDiscount()
Store commission based on vertical
New function to check product name : checkNameAvailability()

##DB Changes:
ALTER TABLE region_vertical ADD commissionPercentage DECIMAL(7,2);
ALTER TABLE region_vertical ADD minChargeAmount DECIMAL(7,2);

UPDATE region_vertical SET commissionPercentage=15.00, minChargeAmount=1.50 ;


##################################################
# product-service-3.5.12 | 03-Mar-2022
##################################################
Add new field in store_delivery_sp : deliverySpTypeId integer

##DB Changes:
ALTER TABLE store_delivery_sp ADD deliverySpTypeId INT;


##################################################
# product-service-3.5.11 | 02-Mar-2022
##################################################
Bug fix for validate discount overlap, return http error 417 if overlap


##################################################
# product-service-3.5.10 | 02-Mar-2022
##################################################
Add inactiveDiscount in product inventory response


##################################################
# product-service-3.5.9 | 01-Mar-2022
##################################################
1. Bug fix for delivery period
2. Put delivery_period name & desciption in table
3. Add new function validateStoreDiscount() to check overlap

CREATE TABLE delivery_period (
id VARCHAR(20) PRIMARY KEY,
NAME VARCHAR(50),
DESCRIPTION VARCHAR(100)
);


##################################################
# product-service-3.5.8 | 25-Feb-2022
##################################################
Bug fix for vehicleType in product


##################################################
# product-service-3.5.7 | 25-Feb-2022
##################################################
For logo & favicon, if front-end POST new asset, backend will overwrite
Bug fix for vehicleType in product


##################################################
# product-service-3.5.6 | 24-Feb-2022
##################################################
Delete storeDeliveryPeriods during delete store 
Change WA template to 'symplified_new_store_notification' for new store creation


##################################################
# product-service-3.5.5 | 23-Feb-2022
##################################################
Add fulfilment in store delivery sp (to store deliveryType = ADHOC)
Add fulfilment in delivery_sp_type (to store deliveryType = ADHOC)

##Database changes :
ALTER TABLE `store_delivery_sp` ADD fulfilment ENUM('EXPRESS','FOURHOURS','NEXTDAY','FOURDAYS');


##################################################
# product-service-3.5.4 | 22-Feb-2022
##################################################
Add description in store delivery period


##################################################
# product-service-3.5.3 | 21-Feb-2022
##################################################
Last version before remove store_asset


##################################################
# product-service-3.5.2 | 21-Feb-2022
##################################################
Bug fix for update store displayAddress


##################################################
# product-service-3.5.1 | 18-Feb-2022
##################################################
1. add new field in discount : isExpired
if discount end date < current date, then isExpired=true

2. Add new field : displayAddress in store table

##Database changes :
ALTER TABLE `store` ADD displayAddress VARCHAR(1000);


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