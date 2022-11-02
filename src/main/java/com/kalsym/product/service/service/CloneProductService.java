package com.kalsym.product.service.service;

import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.product.AddOnTemplateGroup;
import com.kalsym.product.service.model.product.AddOnTemplateItem;
import com.kalsym.product.service.model.product.CompareProductAddonGroup;
import com.kalsym.product.service.model.product.CompareProductOwnerAndBranch;
import com.kalsym.product.service.model.product.CompareProductPackageOption;
import com.kalsym.product.service.model.product.CompareVariantAvailableIdOwnerAndBranch;
import com.kalsym.product.service.model.product.CompareVariantIdOwnerAndBranch;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductAddOn;
import com.kalsym.product.service.model.product.ProductAddOnGroup;
import com.kalsym.product.service.model.product.ProductAsset;
import com.kalsym.product.service.model.store.CompareStoreCategory;
import com.kalsym.product.service.model.store.CompareStoreTemplateGroup;
import com.kalsym.product.service.model.store.CompareStoreTemplateItem;
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
import com.kalsym.product.service.repository.AddOnTemplateGroupRepository;
import com.kalsym.product.service.repository.AddOnTemplateItemRepository;
import com.kalsym.product.service.repository.ProductAddOnGroupRepository;
import com.kalsym.product.service.repository.ProductAddOnRepository;
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
import java.util.Comparator;
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

    @Autowired
    AddOnTemplateGroupRepository addOnTemplateGroupRepository;

    @Autowired
    AddOnTemplateItemRepository addOnTemplateItemRepository;

    @Autowired 
    ProductAddOnRepository productAddOnRepository;

    @Autowired 
    ProductAddOnGroupRepository productAddOnGroupRepository;


    // use this for scenario when newly merchant (branch) wants to create products based on HQ PRODUCTS
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

        //get all template group  with template item  based on store owner id
        List<AddOnTemplateGroup> storeOwnerTemplateGroup = addOnTemplateGroupRepository.findByStoreIdAndStatusNot(storeOwnerId, "DELETED");
        List<AddOnTemplateGroup> mapNewTemplateGroup = new ArrayList<>();
    
        //compare data (Template Group)
        List<CompareStoreTemplateGroup> compareStoreOwnerTemplateGroup = new ArrayList<>();

        //compare data of product add on owner and branch
        List<CompareProductAddonGroup> compareStoreOwnerProductAddonGroup = new ArrayList<>();
    
        mapNewTemplateGroup = storeOwnerTemplateGroup.stream()
        .map(addontemplategroup->{

            AddOnTemplateGroup bodyAddOnTemplateGroup = new AddOnTemplateGroup();
            bodyAddOnTemplateGroup.setTitle(addontemplategroup.getTitle());
            bodyAddOnTemplateGroup.setStoreId(storeId);
            bodyAddOnTemplateGroup.setStatus(addontemplategroup.getStatus());
            
            //saving the data for branch
            AddOnTemplateGroup saveAddOnTemplateGroup = addOnTemplateGroupRepository.save(bodyAddOnTemplateGroup);

            //get template items and save em
            List<AddOnTemplateItem> templateItemStoreOwner  = addontemplategroup.getAddOnTemplateItem();

            List<CompareStoreTemplateItem> compareStoreOwnerTemplateItem = new ArrayList<>();
            
            //set data for comparing purose
            CompareStoreTemplateGroup compareData = new CompareStoreTemplateGroup();
            compareData.setStoreTemplateGroupId(addontemplategroup.getId());
            compareData.setTitle(addontemplategroup.getTitle());
            compareData.setBranchTemplateGroupId(saveAddOnTemplateGroup.getId());

            templateItemStoreOwner.stream()
            .map(addontemplateitem->{

                AddOnTemplateItem bodyAddonTemplateItem = new AddOnTemplateItem();
                bodyAddonTemplateItem.setStatus(addontemplateitem.getStatus());
                bodyAddonTemplateItem.setGroupId(saveAddOnTemplateGroup.getId());
                bodyAddonTemplateItem.setName(addontemplateitem.getName());
                bodyAddonTemplateItem.setPrice(addontemplateitem.getPrice());
                bodyAddonTemplateItem.setDineInPrice(addontemplateitem.getDineInPrice());

                //saving the data for branch
                AddOnTemplateItem saveAddOnTemplateItem = addOnTemplateItemRepository.save(bodyAddonTemplateItem);

                CompareStoreTemplateItem compareDataTemplateItem = new CompareStoreTemplateItem();
                compareDataTemplateItem.setStoreTemplateItem(addontemplateitem.getId());
                compareDataTemplateItem.setBranchTemplateItem(saveAddOnTemplateItem.getId());
                compareDataTemplateItem.setName(addontemplateitem.getName());

                compareStoreOwnerTemplateItem.add(compareDataTemplateItem);

                return addontemplateitem;
            })
            .collect(Collectors.toList());

            compareData.setCompareTemplateItem(compareStoreOwnerTemplateItem);

            compareStoreOwnerTemplateGroup.add(compareData);

            return bodyAddOnTemplateGroup;

        })
        .collect(Collectors.toList());

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
            data.setHasAddOn(x.getHasAddOn());
            
            //after we save branch product, then we will use the product id of branch
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

            //Product add on group
            List<ProductAddOnGroup> ownerProductAddonGroup =productAddOnGroupRepository.findByProductIdAndStatusNot(x.getId(),"DELETED");

            if(ownerProductAddonGroup.size() != 0){

                for(ProductAddOnGroup prodAddonGroup :ownerProductAddonGroup){

                    //to find owner id then we will use the the branch id for creating branch products
                    CompareStoreTemplateGroup filterTemplateGroupOwner = compareStoreOwnerTemplateGroup.stream()
                    .filter(temp -> temp.getStoreTemplateGroupId().equals(prodAddonGroup.getAddonTemplateGroupId()))
                    .findFirst().get();

                    ProductAddOnGroup prodAddonGroupData = new ProductAddOnGroup();
                    prodAddonGroupData.setAddonTemplateGroupId(filterTemplateGroupOwner.getBranchTemplateGroupId());
                    prodAddonGroupData.setMinAllowed(prodAddonGroup.getMinAllowed());
                    prodAddonGroupData.setMaxAllowed(prodAddonGroup.getMaxAllowed());
                    prodAddonGroupData.setSequenceNumber(prodAddonGroup.getSequenceNumber());
                    prodAddonGroupData.setProductId(branchProductId);
                    prodAddonGroupData.setStatus(prodAddonGroup.getStatus());

                    ProductAddOnGroup saveProductAddonGroup = productAddOnGroupRepository.save(prodAddonGroupData);

                    CompareProductAddonGroup dataCompareProductAddonGroup = new CompareProductAddonGroup();
                    dataCompareProductAddonGroup.setStoreProductAddonGroupId(prodAddonGroup.getId());
                    dataCompareProductAddonGroup.setBranchProductAddonGroupId(saveProductAddonGroup.getId());

                    compareStoreOwnerProductAddonGroup.add(dataCompareProductAddonGroup);
                }
            }

            //Product add on
            List<ProductAddOn> ownerProductAddon = productAddOnRepository.findByProductIdAndStatusNot(x.getId(),"DELETED");

            if(ownerProductAddon.size() != 0){

                for(ProductAddOn prodAddon :ownerProductAddon){

                    //to find owner id then we will use the the branch id for creating branch products
                    CompareProductAddonGroup filterProductAddonGroupOwner = compareStoreOwnerProductAddonGroup.stream()
                    .filter(temp -> temp.getStoreProductAddonGroupId().equals(prodAddon.getProductAddonGroupId()))
                    .findFirst().get();

                    //to find owner id then we will use the the branch id
                    CompareStoreTemplateGroup filterDataTemplateGroup = compareStoreOwnerTemplateGroup.stream()
                    .filter(mapper -> mapper.getCompareTemplateItem()
                                    .stream()
                                    .anyMatch(b-> b.getStoreTemplateItem().equals(prodAddon.getAddonTemplateItemId())
                    ))
                    .map(templategroup -> {
        
                        List<CompareStoreTemplateItem> templateItemDetails = templategroup.getCompareTemplateItem()
                        .stream()
                        .sorted(
                            Comparator.comparing((CompareStoreTemplateItem t) -> !t.getStoreTemplateItem().equals(prodAddon.getAddonTemplateItemId()))
                            .thenComparing(CompareStoreTemplateItem::getStoreTemplateItem)
                        )
                        .collect(Collectors.toList());
        
                        templategroup.setCompareTemplateItem(templateItemDetails);
            
                        return templategroup;
                    })
                    .findFirst().get();

                    //set new product add on for branch
                    ProductAddOn prodAddonData = new ProductAddOn();
                    prodAddonData.setProductId(branchProductId);
                    prodAddonData.setPrice(prodAddon.getPrice());
                    prodAddonData.setDineInPrice(prodAddon.getDineInPrice());
                    prodAddonData.setStatus(prodAddon.getStatus());
                    prodAddonData.setSequenceNumber(prodAddon.getSequenceNumber());
                    prodAddonData.setProductAddonGroupId(filterProductAddonGroupOwner.getBranchProductAddonGroupId());
                    prodAddonData.setAddonTemplateItemId(filterDataTemplateGroup.getCompareTemplateItem().get(0).getBranchTemplateItem());

                    ProductAddOn saveProductAddon = productAddOnRepository.save(prodAddonData);
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

    //use this for scenario existing store (branch) wants to copy products from HQ , 
    //the only difference we will query each of related data of products branch and owner , 
    //and merge data by making comparison , 
    //if compare data is NULL it means brnanch data dont have hence we will create it later (if any products will wants to clone to make it as reference)
    //todo : REFACTOR CODE 
    //the code itself is a documentation
    public void cloneProductById(String storeOwnerId, String storeBranchId,Optional<Store> optStoreBranch,List<String> productIds){

        String subProductUrlDomain = "https://" +optStoreBranch.get().getDomain()+"/product/";
        
        List<StoreCategory> storeOwnerCategory = storeCategoryRepository.findByStoreId(storeOwnerId);
        List<StoreCategory> storeBranchCategory = storeCategoryRepository.findByStoreId(storeBranchId);

        List<CompareStoreCategory> compareStoreOwnerCategory = new ArrayList<>();

        List<CompareProductPackageOption> compareProductPackageOption = new ArrayList<>();

        List<AddOnTemplateGroup> storeOwnerTemplateGroup = addOnTemplateGroupRepository.findByStoreIdAndStatusNot(storeOwnerId, "DELETED");
        List<AddOnTemplateGroup> storeBranchTemplateGroup = addOnTemplateGroupRepository.findByStoreIdAndStatusNot(storeBranchId, "DELETED");

        List<CompareStoreTemplateGroup> compareStoreOwnerTemplateGroup = new ArrayList<>();

        List<CompareProductAddonGroup> compareStoreOwnerProductAddonGroup = new ArrayList<>();

        //map data catgeory branch vs owner
        storeOwnerCategory.stream()
        .map(x->{

            //match result same name
            Optional<StoreCategory> optFilterStoreBranchCategory = storeBranchCategory.stream()
            .filter(cat -> cat.getName().contains(x.getName()))
            .findFirst();

            //set comparing data
            CompareStoreCategory compareData = new CompareStoreCategory();
            compareData.setStoreOwnerCategoryId(x.getId());
            compareData.setName(x.getName());
            //we set it to null first ,then we will create it upon create a product if reference branch categoryid is null
            compareData.setBranchCategoryId(optFilterStoreBranchCategory.isPresent()?optFilterStoreBranchCategory.get().getId():null);

            compareStoreOwnerCategory.add(compareData);

            return x;

        })
        .collect(Collectors.toList());

        storeOwnerTemplateGroup.stream()
        .map((AddOnTemplateGroup x)->{

            //match result same title
            Optional<AddOnTemplateGroup> optFilterStoreBranchAddOnTemplateGroup = storeBranchTemplateGroup.stream()
            .filter((AddOnTemplateGroup atg) -> atg.getTitle().contains(x.getTitle()))
            .findFirst();

            //set comparing data
            CompareStoreTemplateGroup compareTemplateGroup = new CompareStoreTemplateGroup();
            compareTemplateGroup.setStoreTemplateGroupId(x.getId());
            compareTemplateGroup.setTitle(x.getTitle());
            //we set it to null first ,then we will create it upon create a product if reference TemplateGroup categoryid is null
            compareTemplateGroup.setBranchTemplateGroupId(optFilterStoreBranchAddOnTemplateGroup.isPresent()?optFilterStoreBranchAddOnTemplateGroup.get().getId():null);

            List<CompareStoreTemplateItem> compareStoreOwnerTemplateItem = new ArrayList<>();

            x.getAddOnTemplateItem().stream()
            .map( (AddOnTemplateItem ownerTemplateItem)->{

                CompareStoreTemplateItem compareDataTemplateItem = new CompareStoreTemplateItem();
                compareDataTemplateItem.setStoreTemplateItem(ownerTemplateItem.getId());
                compareDataTemplateItem.setName(ownerTemplateItem.getName());

                //handle error if template group is null
                if(optFilterStoreBranchAddOnTemplateGroup.isPresent()){
                    
                    //match result same name
                    Optional<AddOnTemplateItem> optFilterStoreBranchTemplateItem = optFilterStoreBranchAddOnTemplateGroup
                    .get().getAddOnTemplateItem()
                    .stream()
                    .filter((AddOnTemplateItem ati) -> ati.getName().contains(ownerTemplateItem.getName()))
                    .findFirst();

                    compareDataTemplateItem.setBranchTemplateItem(optFilterStoreBranchTemplateItem.isPresent()?optFilterStoreBranchTemplateItem.get().getId():null);


                } else{
                    compareDataTemplateItem.setBranchTemplateItem(null);
                }
   
                compareStoreOwnerTemplateItem.add(compareDataTemplateItem);

                return ownerTemplateItem;
            })
            .collect(Collectors.toList());

            compareTemplateGroup.setCompareTemplateItem(compareStoreOwnerTemplateItem);

            compareStoreOwnerTemplateGroup.add(compareTemplateGroup);
            
            return x;
        })
        .collect(Collectors.toList());

        for(String productId : productIds){

            Optional<Product> optProduct = productRepository.findById(productId);

            if(optProduct.isPresent()){

                Product dataOptProduct = optProduct.get();

                //to find category owner id then we will use the the branch id for creating branch products
                CompareStoreCategory filterCategoryOwner = compareStoreOwnerCategory.stream()
                .filter(category -> category.getStoreOwnerCategoryId().equals(dataOptProduct.getCategoryId()))
                .findFirst().get();

                //to be add data
                Product data = new Product();
                data.setName(dataOptProduct.getName());
                data.setDescription(dataOptProduct.getDescription());
                data.setStoreId(storeBranchId);

                //if null branchcategoryId then we save it in database
                if(filterCategoryOwner.getBranchCategoryId() == null){

                    Optional<StoreCategory> ownerCategory = storeCategoryRepository.findById(filterCategoryOwner.getStoreOwnerCategoryId());

                    StoreCategory bodyStoreCategory = new StoreCategory();
                    bodyStoreCategory.setName(ownerCategory.get().getName());
                    bodyStoreCategory.setParentCategoryId(ownerCategory.get().getParentCategoryId());
                    bodyStoreCategory.setStoreId(storeBranchId);
                    bodyStoreCategory.setThumbnailUrl(ownerCategory.get().getThumbnailUrl());

                    StoreCategory saveStoreCategory = storeCategoryRepository.save(bodyStoreCategory);
                
                    for(CompareStoreCategory csoc:compareStoreOwnerCategory){

                        if(csoc.getStoreOwnerCategoryId().equals(ownerCategory.get().getId())){
                            csoc.setBranchCategoryId(saveStoreCategory.getId());
                        }
                    }
                 
                } else{
                    data.setCategoryId(filterCategoryOwner.getBranchCategoryId());

                }
                
                data.setStatus(dataOptProduct.getStatus());
                data.setThumbnailUrl(dataOptProduct.getThumbnailUrl());
                data.setSeoUrl(subProductUrlDomain+dataOptProduct.getSeoName());
                data.setSeoName(dataOptProduct.getSeoName());
                data.setTrackQuantity(dataOptProduct.getTrackQuantity());
                data.setAllowOutOfStockPurchases(dataOptProduct.getAllowOutOfStockPurchases());
                data.setMinQuantityForAlarm(dataOptProduct.getMinQuantityForAlarm());
                data.setPackingSize(dataOptProduct.getPackingSize());
                data.setIsPackage(dataOptProduct.getIsPackage());
                data.setIsNoteOptional(dataOptProduct.getIsNoteOptional());
                data.setCustomNote(dataOptProduct.getCustomNote());
                data.setVehicleType(dataOptProduct.getVehicleType());
                data.setHasAddOn(dataOptProduct.getHasAddOn());
                
                //after we save branch product, then we will use the product id of branch
                Product newlyProductData = productRepository.save(data);
                String branchProductId = newlyProductData.getId();
                
                //get product asset (store owner) then clone it
                List<ProductAsset> productAssets = productAssetRepository.findByProductId(dataOptProduct.getId());
                
                if(productAssets.size() != 0){
                    for(ProductAsset pa : productAssets){

                        ProductAsset productAsseData = new ProductAsset();
                        productAsseData.setName(pa.getName());
                        productAsseData.setUrl(pa.getUrl());
                        productAsseData.setProductId(branchProductId);
                        productAsseData.setIsThumbnail(pa.getIsThumbnail());

                        if(pa.getItemCode() != null){

                            //set the itemCode (ownerProductId+{int} -> branchProductId+{int})
                            productAsseData.setItemCode(pa.getItemCode().replaceAll(dataOptProduct.getId(),branchProductId));

                        }
                        productAssetRepository.save(productAsseData);
                        
                    }
                }

                //get product inventory (store owner) then clone it
                List<ProductInventory> ownerProductInventory = productInventoryMainRepository.findByProductId(dataOptProduct.getId());
                
                if(ownerProductInventory.size() != 0){

                    for(ProductInventory pi :ownerProductInventory){

                        ProductInventory productInventoryData = new ProductInventory();
                        productInventoryData.setItemCode(pi.getItemCode().replaceAll(dataOptProduct.getId(), branchProductId));
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
                List<ProductVariant> ownerProductVariant = productVariantRepository.findByProductId(dataOptProduct.getId());

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
                List<ProductVariantAvailable> ownerProductVariantAvailable = productVariantAvailableRepository.findByProductId(dataOptProduct.getId());

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
                List<ProductInventoryItemMain> ownerProductInventoryItem = productInventoryItemMainRepository.findByProductId(dataOptProduct.getId());

                if(ownerProductInventoryItem.size() != 0){

                    for(ProductInventoryItemMain pii : ownerProductInventoryItem){

                        CompareVariantAvailableIdOwnerAndBranch filterCompareVariantAvailableIdOwnerAndBranch = compareVariantAvailableIdOwnerAndBranch.stream()
                        .filter(variantavailable -> variantavailable.getOwnerVariantAvailableId().equals(pii.getProductVariantAvailableId()))
                        .findFirst().get(); 

                        ProductInventoryItemMain piiData = new ProductInventoryItemMain();

                        piiData.setItemCode(pii.getItemCode().replaceAll(dataOptProduct.getId(), branchProductId));
                        piiData.setProductVariantAvailableId(filterCompareVariantAvailableIdOwnerAndBranch.getBranchVariantAvailableId());
                        piiData.setProductId(branchProductId);
                        piiData.setSequenceNumber(piiData.getSequenceNumber());

                        ProductInventoryItemMain saveProductInventoryItemMain = productInventoryItemMainRepository.save(piiData);

                    }

                }

                //get product package option
                List<ProductPackageOption> ownerProductPackageOption = productPackageOptionRepository.findByPackageId(dataOptProduct.getId());

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

                //Product add on group
                List<ProductAddOnGroup> ownerProductAddonGroup =productAddOnGroupRepository.findByProductIdAndStatusNot(productId,"DELETED");

                if(ownerProductAddonGroup.size() != 0){

                    for(ProductAddOnGroup prodAddonGroup :ownerProductAddonGroup){

                        //to find owner id then we will use the the branch id for creating branch products
                        CompareStoreTemplateGroup filterTemplateGroupOwner = compareStoreOwnerTemplateGroup.stream()
                        .filter(temp -> temp.getStoreTemplateGroupId().equals(prodAddonGroup.getAddonTemplateGroupId()))
                        .findFirst().get();

                        ProductAddOnGroup prodAddonGroupData = new ProductAddOnGroup();

                        //we only save template group for store branch if selected product has addon related to the template group
                        if(filterTemplateGroupOwner.getBranchTemplateGroupId() == null){

                            AddOnTemplateGroup getDetailsOfStoreOwnerAddonTemplateGroup = addOnTemplateGroupRepository.findById(filterTemplateGroupOwner.getStoreTemplateGroupId()).get();
                            AddOnTemplateGroup bodyAddOnTemplateGroup = new AddOnTemplateGroup();
                            bodyAddOnTemplateGroup.setTitle(getDetailsOfStoreOwnerAddonTemplateGroup.getTitle());
                            bodyAddOnTemplateGroup.setStoreId(storeBranchId);
                            bodyAddOnTemplateGroup.setStatus(getDetailsOfStoreOwnerAddonTemplateGroup.getStatus());
                            
                            AddOnTemplateGroup saveAddOnTemplateGroup = addOnTemplateGroupRepository.save(bodyAddOnTemplateGroup);

                            for(CompareStoreTemplateGroup csoc:compareStoreOwnerTemplateGroup){

                                if(csoc.getStoreTemplateGroupId().equals(getDetailsOfStoreOwnerAddonTemplateGroup.getId())){
                                    csoc.setBranchTemplateGroupId(saveAddOnTemplateGroup.getId());
                                }
                            }

                            prodAddonGroupData.setAddonTemplateGroupId(saveAddOnTemplateGroup.getId());


                        } else{
                            
                            prodAddonGroupData.setAddonTemplateGroupId(filterTemplateGroupOwner.getBranchTemplateGroupId());

                        }

                        prodAddonGroupData.setMinAllowed(prodAddonGroup.getMinAllowed());
                        prodAddonGroupData.setMaxAllowed(prodAddonGroup.getMaxAllowed());
                        prodAddonGroupData.setSequenceNumber(prodAddonGroup.getSequenceNumber());
                        prodAddonGroupData.setProductId(branchProductId);
                        prodAddonGroupData.setStatus(prodAddonGroup.getStatus());

                        ProductAddOnGroup saveProductAddonGroup = productAddOnGroupRepository.save(prodAddonGroupData);

                        CompareProductAddonGroup dataCompareProductAddonGroup = new CompareProductAddonGroup();
                        dataCompareProductAddonGroup.setStoreProductAddonGroupId(prodAddonGroup.getId());
                        dataCompareProductAddonGroup.setBranchProductAddonGroupId(saveProductAddonGroup.getId());

                        compareStoreOwnerProductAddonGroup.add(dataCompareProductAddonGroup);
                    }
                }

            //Product add on
            List<ProductAddOn> ownerProductAddon = productAddOnRepository.findByProductIdAndStatusNot(productId,"DELETED");

            if(ownerProductAddon.size() != 0){

                for(ProductAddOn prodAddon :ownerProductAddon){

                    //to find owner id then we will use the the branch id for creating branch products
                    CompareProductAddonGroup filterProductAddonGroupOwner = compareStoreOwnerProductAddonGroup.stream()
                    .filter(temp -> temp.getStoreProductAddonGroupId().equals(prodAddon.getProductAddonGroupId()))
                    .findFirst().get();

                    //to find compare tempate item by matching the id , then sort it on same id will be on top
                    CompareStoreTemplateGroup filterDataTemplateGroup = compareStoreOwnerTemplateGroup.stream()
                    .filter((CompareStoreTemplateGroup mapper) -> mapper.getCompareTemplateItem()
                                    .stream()
                                    .anyMatch(b-> b.getStoreTemplateItem().equals(prodAddon.getAddonTemplateItemId())
                    ))
                    .map((CompareStoreTemplateGroup templategroup) -> {
        
                        List<CompareStoreTemplateItem> templateItemDetails = templategroup.getCompareTemplateItem()
                        .stream()
                        .sorted(
                            Comparator.comparing((CompareStoreTemplateItem t) -> !t.getStoreTemplateItem().equals(prodAddon.getAddonTemplateItemId()))
                            .thenComparing(CompareStoreTemplateItem::getStoreTemplateItem)
                        )
                        .collect(Collectors.toList());
        
                        templategroup.setCompareTemplateItem(templateItemDetails);
            
                        return templategroup;
                    })
                    .findFirst().get();

                    //set new product add on for branch
                    ProductAddOn prodAddonData = new ProductAddOn();
                    prodAddonData.setProductId(branchProductId);
                    prodAddonData.setPrice(prodAddon.getPrice());
                    prodAddonData.setDineInPrice(prodAddon.getDineInPrice());
                    prodAddonData.setStatus(prodAddon.getStatus());
                    prodAddonData.setSequenceNumber(prodAddon.getSequenceNumber());
                    prodAddonData.setProductAddonGroupId(filterProductAddonGroupOwner.getBranchProductAddonGroupId());

                    //if null then create template item 
                    if(filterDataTemplateGroup.getCompareTemplateItem().get(0).getBranchTemplateItem() == null){

                        //create first by getting detail of owner
                        AddOnTemplateItem getDetailsOfStoreOwnerAddonTemplateItem = addOnTemplateItemRepository.findById(filterDataTemplateGroup.getCompareTemplateItem().get(0).getStoreTemplateItem()).get();
                        AddOnTemplateItem bodyAddonTemplateItem = new AddOnTemplateItem();
                        bodyAddonTemplateItem.setStatus(getDetailsOfStoreOwnerAddonTemplateItem.getStatus());
                        bodyAddonTemplateItem.setName(getDetailsOfStoreOwnerAddonTemplateItem.getName());
                        bodyAddonTemplateItem.setPrice(getDetailsOfStoreOwnerAddonTemplateItem.getPrice());
                        bodyAddonTemplateItem.setDineInPrice(getDetailsOfStoreOwnerAddonTemplateItem.getDineInPrice());
                        bodyAddonTemplateItem.setGroupId(filterDataTemplateGroup.getBranchTemplateGroupId());
                
                        //saving the data for branch
                        AddOnTemplateItem saveAddOnTemplateItem = addOnTemplateItemRepository.save(bodyAddonTemplateItem);
                        prodAddonData.setAddonTemplateItemId(saveAddOnTemplateItem.getId());


                        //then mao the value of comaoter template group
                        for(CompareStoreTemplateGroup cstg:compareStoreOwnerTemplateGroup){

                            if(cstg.getCompareTemplateItem().get(0).getStoreTemplateItem().equals(prodAddon.getAddonTemplateItemId())){
                                cstg.getCompareTemplateItem().get(0).setBranchTemplateItem(subProductUrlDomain);
                            }

                        }
                        
                    } 
                    else{
                        prodAddonData.setAddonTemplateItemId(filterDataTemplateGroup.getCompareTemplateItem().get(0).getBranchTemplateItem());

                    }

                    ProductAddOn saveProductAddon = productAddOnRepository.save(prodAddonData);
                }

            }

            }



        }




  
        
    }

    // public void clone 

    // public void assignSelectedProductPackaeOptionDetails(List<CompareProductPackageOption> compareProductPackageOption){  


    //     //later assign product combo

    //     //to add product package option details
    //     compareProductPackageOption.stream()
    //     .map(y->{

    //         List<ProductPackageOptionDetail> dataProductPacakageDetails = productPackageOptionDetailRepository.findByProductPackageOptionId(y.getOwnerProductPackageOptionId());

    //         if(dataProductPacakageDetails.size() != 0){

    //             for(ProductPackageOptionDetail ppd :dataProductPacakageDetails ){

    //                 //to find product owner id and we map with branch product, let say the value of branch is null then we will create new product
    //                 Optional<Product> optPro = productRepository.findById(ppd.getProductId());
    //                 CompareProductOwnerAndBranch filterProductOwnerAndBranch = compareProductOwnerAndBranch.stream()
    //                 .filter(product -> product.getOwnerProductId().equals(ppd.getProductId()))
    //                 .findFirst().get();
                    
    //                 ProductPackageOptionDetail packageOptionDetailData = new ProductPackageOptionDetail();
    //                 packageOptionDetailData.setProductId(filterProductOwnerAndBranch.getBranchProductId());
    //                 packageOptionDetailData.setProductPackageOptionId(y.getBranchProductPackageOptionId());
    //                 packageOptionDetailData.setIsDefault(ppd.getIsDefault());
    //                 packageOptionDetailData.setSequenceNumber(ppd.getSequenceNumber());

    //                 productPackageOptionDetailRepository.save(packageOptionDetailData);
    //             }

          
    //         }    
    //         return y;
    //     })
    //     .collect(Collectors.toList());

    // }
   
}


