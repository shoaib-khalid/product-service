package com.kalsym.product.service.service;

import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.product.CompareProductOwnerAndBranch;
import com.kalsym.product.service.model.product.CompareProductPackageOption;
import com.kalsym.product.service.model.product.CompareVariantAvailableIdOwnerAndBranch;
import com.kalsym.product.service.model.product.CompareVariantIdOwnerAndBranch;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductAsset;
import com.kalsym.product.service.model.store.CompareStoreCategory;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.product.ProductInventoryItemMain;
import com.kalsym.product.service.model.product.ProductPackageOption;
import com.kalsym.product.service.model.product.ProductPackageOptionDetail;
import com.kalsym.product.service.model.product.ProductSpecs;
import com.kalsym.product.service.model.product.ProductVariant;
import com.kalsym.product.service.model.product.ProductVariantAvailable;
import com.kalsym.product.service.repository.ProductPackageOptionRepository;


import com.kalsym.product.service.model.store.StoreDiscount;
import com.kalsym.product.service.enums.StoreDiscountType;

import com.kalsym.product.service.model.store.StoreDiscountTier;
import com.kalsym.product.service.model.store.StoreWithDetails;
import com.kalsym.product.service.repository.ProductAssetRepository;
import com.kalsym.product.service.repository.ProductInventoryItemMainRepository;
import com.kalsym.product.service.repository.ProductInventoryRepository;
import com.kalsym.product.service.repository.ProductPackageOptionDetailRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.repository.ProductVariantRepository;
import com.kalsym.product.service.repository.StoreCategoryRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.repository.ProductVariantAvailableRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import com.kalsym.product.service.utility.DateTimeUtil;

import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat; 

@Service
public class CloneProductService {

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Autowired
    ProductAssetRepository productAssetRepository;

    @Autowired
    ProductInventoryRepository productInventoryMainRepository;

    @Autowired
    ProductVariantRepository productVariantRepository;

    @Autowired
    ProductInventoryItemMainRepository productInventoryItemMainRepository;

    @Autowired
    ProductVariantAvailableRepository productVariantAvailableRepository;

    @Autowired
    ProductPackageOptionRepository productPackageOptionRepository;

    @Autowired
    ProductPackageOptionDetailRepository productPackageOptionDetailRepository;

    public void cloneProducts (String storeId, String storeOwnerId,Optional<Store> optStore){

        //get all the categories based on owner store id
        List<StoreCategory> storeOwnerCategory = storeCategoryRepository.findByStoreId(storeOwnerId);
        List<StoreCategory> mapNewStoreCategory = new ArrayList<>();
        
        //get all the products based on owner store id
        List<Product> storeOwnerProducts = productRepository.findByStoreIdAndStatusNot(storeOwnerId,"DELETED");
        List<Product> mapNewProducts = new ArrayList<>();

        //compare store owner product and branch product
        List<CompareProductOwnerAndBranch> compareProductOwnerAndBranch = new ArrayList<>();

        //compare store owner category and branch category
        List<CompareStoreCategory> compareStoreOwnerCategory = new ArrayList<>();

        //compare store owner product PackageOption and branch PackageOption
        List<CompareProductPackageOption> compareProductPackageOption = new ArrayList<>();

        //concat the domain url in order to concat for seoUrl
        String subProductUrlDomain = "https://" +optStore.get().getDomain()+"/product/";

        //save the newly store category in branch
        mapNewStoreCategory = storeOwnerCategory.stream()
        .map(x->{

            StoreCategory bodyStoreCategory = new StoreCategory();
            bodyStoreCategory.setName(x.getName());
            bodyStoreCategory.setParentCategoryId(x.getParentCategoryId());
            bodyStoreCategory.setStoreId(storeId);
            bodyStoreCategory.setThumbnailUrl(x.getThumbnailUrl());

            StoreCategory saveStoreCategory = storeCategoryRepository.save(bodyStoreCategory);

            //push data for comparing purose of store category
            CompareStoreCategory compareData = new CompareStoreCategory();
            compareData.setStoreOwnerCategoryId(x.getId());
            compareData.setName(x.getName());
            compareData.setBranchCategoryId(saveStoreCategory.getId());
            compareStoreOwnerCategory.add(compareData);

            return bodyStoreCategory;


        })
        .collect(Collectors.toList());

        mapNewProducts = storeOwnerProducts.stream()
        .map(x->{

            //to find category owner id then we will use the the branch id for creating branch products
            CompareStoreCategory filterCategoryOwner = compareStoreOwnerCategory.stream()
            .filter(category -> category.getStoreOwnerCategoryId().equals(x.getCategoryId()))
            .findFirst().get();

            //to be add data
            Product data = new Product();
            data.setName(x.getName());
            data.setDescription(x.getDescription());
            data.setStoreId(storeId);
            data.setCategoryId(filterCategoryOwner.getBranchCategoryId());
            data.setStatus(x.getStatus());
            data.setThumbnailUrl(x.getThumbnailUrl());
            data.setSeoUrl(subProductUrlDomain+x.getSeoName());
            data.setSeoName(x.getSeoName());
            data.setTrackQuantity(x.getTrackQuantity());
            data.setAllowOutOfStockPurchases(x.getAllowOutOfStockPurchases());
            data.setMinQuantityForAlarm(x.getMinQuantityForAlarm());
            data.setPackingSize(x.getPackingSize());
            data.setIsPackage(x.getIsPackage());
            data.setIsNoteOptional(x.getIsNoteOptional());
            data.setCustomNote(x.getCustomNote());
            data.setVehicleType(x.getVehicleType());
            
            Product newlyProductData = productRepository.save(data);
            String branchProductId = newlyProductData.getId();
            
            //get product asset (store owner) then clone it
            List<ProductAsset> productAssets = productAssetRepository.findByProductId(x.getId());
            
            if(productAssets.size() != 0){
                for(ProductAsset pa : productAssets){

                    ProductAsset productAsseData = new ProductAsset();
                    productAsseData.setName(pa.getName());
                    productAsseData.setUrl(pa.getUrl());
                    productAsseData.setProductId(branchProductId);
                    productAsseData.setIsThumbnail(pa.getIsThumbnail());

                    if(pa.getItemCode() != null){

                        //set the itemCode (ownerProductId+{int} -> branchProductId+{int})
                        productAsseData.setItemCode(pa.getItemCode().replaceAll(x.getId(),branchProductId));

                    }
                    productAssetRepository.save(productAsseData);
                    
                }
            }

            //get product inventory (store owner) then clone it
            List<ProductInventory> ownerProductInventory = productInventoryMainRepository.findByProductId(x.getId());
           
            if(ownerProductInventory.size() != 0){

                for(ProductInventory pi :ownerProductInventory){

                    ProductInventory productInventoryData = new ProductInventory();
                    productInventoryData.setItemCode(pi.getItemCode().replaceAll(x.getId(), branchProductId));
                    productInventoryData.setPrice(pi.getPrice());
                    productInventoryData.setDineInPrice(pi.getDineInPrice());
                    productInventoryData.setCompareAtprice(pi.getCompareAtprice());
                    productInventoryData.setSKU(pi.getSKU());
                    productInventoryData.setQuantity(pi.getQuantity());
                    productInventoryData.setProductId(branchProductId);
                    productInventoryData.setStatus(pi.getStatus());

                    productInventoryMainRepository.save(productInventoryData);

                }
            }

            //get vaiant (store owner) then clone it
            List<ProductVariant> ownerProductVariant = productVariantRepository.findByProductId(x.getId());

            //to keep the value for comparing puprose
            List<CompareVariantIdOwnerAndBranch> compareVariantIdOwnerAndBranch = new ArrayList<>();


            if(ownerProductVariant.size() != 0){

                for(ProductVariant pv : ownerProductVariant){

                    ProductVariant productVariantData = new ProductVariant();
                    productVariantData.setName(pv.getName());
                    productVariantData.setDescription(pv.getDescription());
                    productVariantData.setProduct(newlyProductData);
                    productVariantData.setSequenceNumber(pv.getSequenceNumber());

                    ProductVariant productVariantSave = productVariantRepository.save(productVariantData);

                    //add list of compareVariantId
                    CompareVariantIdOwnerAndBranch compareVariantId = new CompareVariantIdOwnerAndBranch();
                    compareVariantId.setBranchVariantId(productVariantSave.getId());
                    compareVariantId.setOwnerVariantId(pv.getId());
                    compareVariantIdOwnerAndBranch.add(compareVariantId);


                }
                
            }

            //get variant available (store owner)
            List<ProductVariantAvailable> ownerProductVariantAvailable = productVariantAvailableRepository.findByProductId(x.getId());

            //to keep the value for comparing puprose
            List<CompareVariantAvailableIdOwnerAndBranch> compareVariantAvailableIdOwnerAndBranch = new ArrayList<>();

            if(ownerProductVariantAvailable.size() != 0){

                for (ProductVariantAvailable pva :ownerProductVariantAvailable){

                    //to find category owner id then we will use the the branch id for creating branch products
                    CompareVariantIdOwnerAndBranch filterCompareVariantIdOwnerAndBranch = compareVariantIdOwnerAndBranch.stream()
                    .filter(variant -> variant.getOwnerVariantId().equals(pva.getProductVariantId()))
                    .findFirst().get();
                                    
                    ProductVariantAvailable productVariantAvailableData = new ProductVariantAvailable();
                    productVariantAvailableData.setProductId(branchProductId);
                    productVariantAvailableData.setProductVariantId(filterCompareVariantIdOwnerAndBranch.getBranchVariantId());
                    productVariantAvailableData.setSequenceNumber(pva.getSequenceNumber());
                    productVariantAvailableData.setValue(pva.getValue());

                    ProductVariantAvailable saveProductVariantAvailable = productVariantAvailableRepository.save(productVariantAvailableData);

                    //add list of 
                    CompareVariantAvailableIdOwnerAndBranch compareVariantAvailableId = new CompareVariantAvailableIdOwnerAndBranch();
                    compareVariantAvailableId.setBranchVariantAvailableId(saveProductVariantAvailable.getId());
                    compareVariantAvailableId.setOwnerVariantAvailableId(pva.getId());

                    compareVariantAvailableIdOwnerAndBranch.add(compareVariantAvailableId);
                }              

            }

            //get inventory item (store owner) then clone it
            List<ProductInventoryItemMain> ownerProductInventoryItem = productInventoryItemMainRepository.findByProductId(x.getId());

            if(ownerProductInventoryItem.size() != 0){

                for(ProductInventoryItemMain pii : ownerProductInventoryItem){

                    CompareVariantAvailableIdOwnerAndBranch filterCompareVariantAvailableIdOwnerAndBranch = compareVariantAvailableIdOwnerAndBranch.stream()
                    .filter(variantavailable -> variantavailable.getOwnerVariantAvailableId().equals(pii.getProductVariantAvailableId()))
                    .findFirst().get(); 

                    ProductInventoryItemMain piiData = new ProductInventoryItemMain();

                    piiData.setItemCode(pii.getItemCode().replaceAll(x.getId(), branchProductId));
                    piiData.setProductVariantAvailableId(filterCompareVariantAvailableIdOwnerAndBranch.getBranchVariantAvailableId());
                    piiData.setProductId(branchProductId);
                    piiData.setSequenceNumber(piiData.getSequenceNumber());

                    ProductInventoryItemMain saveProductInventoryItemMain = productInventoryItemMainRepository.save(piiData);

                }

            }

            //get product package option
            List<ProductPackageOption> ownerProductPackageOption = productPackageOptionRepository.findByPackageId(x.getId());

            if(ownerProductPackageOption.size() != 0){

                for(ProductPackageOption ppo :ownerProductPackageOption){

                    ProductPackageOption ppoData = new ProductPackageOption();
                    ppoData.setTitle(ppo.getTitle());
                    ppoData.setTotalAllow(ppo.getTotalAllow());
                    ppoData.setPackageId(branchProductId);
                    ppoData.setSequenceNumber(ppo.getSequenceNumber());

                    ProductPackageOption saveProductPackageOption= productPackageOptionRepository.save(ppoData);

                    CompareProductPackageOption dataCompareProductPackageOption = new CompareProductPackageOption();
                    dataCompareProductPackageOption.setOwnerProductPackageOptionId(ppo.getId());
                    dataCompareProductPackageOption.setBranchProductPackageOptionId(saveProductPackageOption.getId());
                    compareProductPackageOption.add(dataCompareProductPackageOption);

                }

            }

            CompareProductOwnerAndBranch comparingProductOwnerAndBranch = new CompareProductOwnerAndBranch();
            comparingProductOwnerAndBranch.setBranchProductId(branchProductId);
            comparingProductOwnerAndBranch.setOwnerProductId(x.getId());
            compareProductOwnerAndBranch.add(comparingProductOwnerAndBranch);

            return data;
        })
        .collect(Collectors.toList());

        compareProductPackageOption.stream()
        .map(y->{

            List<ProductPackageOptionDetail> dataProductPacakageDetails = productPackageOptionDetailRepository.findByProductPackageOptionId(y.getOwnerProductPackageOptionId());

            if(dataProductPacakageDetails.size() != 0){

                for(ProductPackageOptionDetail ppd :dataProductPacakageDetails ){

                    //to find product owner id then we will use the the branch id 
                    CompareProductOwnerAndBranch filterProductOwnerAndBranch = compareProductOwnerAndBranch.stream()
                    .filter(product -> product.getOwnerProductId().equals(ppd.getProductId()))
                    .findFirst().get();

                    ProductPackageOptionDetail packageOptionDetailData = new ProductPackageOptionDetail();
                    packageOptionDetailData.setProductId(filterProductOwnerAndBranch.getBranchProductId());
                    packageOptionDetailData.setProductPackageOptionId(y.getBranchProductPackageOptionId());
                    packageOptionDetailData.setIsDefault(ppd.getIsDefault());
                    packageOptionDetailData.setSequenceNumber(ppd.getSequenceNumber());

                    productPackageOptionDetailRepository.save(packageOptionDetailData);
                }

          
            }    
            return y;
        })
        .collect(Collectors.toList());
    }

    public void bulkDeleteProducts(List<String> productIds){

        for(String productId : productIds){

            Optional<Product> optProdcut = productRepository.findById(productId);

            if (optProdcut.isPresent()) {
                Product p = optProdcut.get();
                p.setStatus("DELETED");
                productRepository.save(p);
            }  

        }
    }

    public void bulkDeleteCategory(List<String> categoryIds){

        for(String categoryId : categoryIds){

            Optional<StoreCategory> optStoreCategory = storeCategoryRepository.findById(categoryId);

            if (optStoreCategory.isPresent()) {
                
                storeCategoryRepository.delete(optStoreCategory.get());

            }  

        }
    }
    
}
