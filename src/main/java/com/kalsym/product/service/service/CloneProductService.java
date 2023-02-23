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

import javax.persistence.EntityManager;

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

    @Autowired
    ProductAddOnGroupService productAddOnGroupService;

    @Autowired
    ProductAddOnService productAddOnService;

    @Autowired
    EntityManager entityManager;

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
        .map((AddOnTemplateGroup addontemplategroup)->{

            //Entities which previously referenced ,the detached entity will continue to reference it.
            entityManager.detach(addontemplategroup);
            //since we already detach the entity then we will proceed to set the data that belongs to that storeId
            AddOnTemplateGroup bodyAddOnTemplateGroup = addontemplategroup;
            bodyAddOnTemplateGroup.setStoreId(storeId);
            
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
            .map((AddOnTemplateItem addontemplateitem)->{

                //Entities which previously referenced ,the detached entity will continue to reference it.
                entityManager.detach(addontemplateitem);
                //since we already detach the entity then we will assign data accordingly
                AddOnTemplateItem bodyAddonTemplateItem = addontemplateitem;
                bodyAddonTemplateItem.setGroupId(saveAddOnTemplateGroup.getId());

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
        .map((StoreCategory x)->{

            //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
            entityManager.detach(x);
            StoreCategory bodyStoreCategory = x;
            bodyStoreCategory.setStoreId(storeId);

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
        .map((Product x)->{

            //to find category owner id then we will use the the branch id for creating branch products
            CompareStoreCategory filterCategoryOwner = compareStoreOwnerCategory.stream()
            .filter(category -> category.getStoreOwnerCategoryId().equals(x.getCategoryId()))
            .findFirst().get();

            //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
            entityManager.detach(x);
            //to be add data
            Product data = x;
            data.setStoreId(storeId);
            data.setCategoryId(filterCategoryOwner.getBranchCategoryId());
         
            //after we save branch product, then we will use the product id of branch
            Product newlyProductData = productRepository.save(data);
            String branchProductId = newlyProductData.getId();
            
            //get product asset (store owner) then clone it
            List<ProductAsset> productAssets = productAssetRepository.findByProductId(x.getId());
            
            if(productAssets.size() != 0){
                for(ProductAsset pa : productAssets){

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(pa);
                    ProductAsset productAsseData = pa;
                    productAsseData.setProductId(branchProductId);

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

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(pi);
                    ProductInventory productInventoryData = pi;
                    productInventoryData.setItemCode(pi.getItemCode().replaceAll(x.getId(), branchProductId));
                    productInventoryData.setProductId(branchProductId);

                    productInventoryMainRepository.save(productInventoryData);

                }
            }

            //get vaiant (store owner) then clone it
            List<ProductVariant> ownerProductVariant = productVariantRepository.findByProductId(x.getId());

            //to keep the value for comparing puprose
            List<CompareVariantIdOwnerAndBranch> compareVariantIdOwnerAndBranch = new ArrayList<>();


            if(ownerProductVariant.size() != 0){

                for(ProductVariant pv : ownerProductVariant){

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(pv);
                    ProductVariant productVariantData = pv;
                    productVariantData.setProduct(newlyProductData);

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
                             
                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(pva);
                    ProductVariantAvailable productVariantAvailableData = pva;
                    productVariantAvailableData.setProductId(branchProductId);
                    productVariantAvailableData.setProductVariantId(filterCompareVariantIdOwnerAndBranch.getBranchVariantId());
              
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

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(pii);
                    ProductInventoryItemMain piiData = pii;

                    piiData.setItemCode(pii.getItemCode().replaceAll(x.getId(), branchProductId));
                    piiData.setProductVariantAvailableId(filterCompareVariantAvailableIdOwnerAndBranch.getBranchVariantAvailableId());
                    piiData.setProductId(branchProductId);

                    ProductInventoryItemMain saveProductInventoryItemMain = productInventoryItemMainRepository.save(piiData);

                }

            }

            //get product package option
            List<ProductPackageOption> ownerProductPackageOption = productPackageOptionRepository.findByPackageId(x.getId());

            if(ownerProductPackageOption.size() != 0){

                for(ProductPackageOption ppo :ownerProductPackageOption){

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(ppo);
                    ProductPackageOption ppoData =ppo;
                    ppoData.setPackageId(branchProductId);

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

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(prodAddonGroup);

                    ProductAddOnGroup prodAddonGroupData = prodAddonGroup;
                    prodAddonGroupData.setAddonTemplateGroupId(filterTemplateGroupOwner.getBranchTemplateGroupId());
                    prodAddonGroupData.setProductId(branchProductId);

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

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(prodAddon);

                    //set new product add on for branch
                    ProductAddOn prodAddonData = prodAddon;
                    prodAddonData.setProductId(branchProductId);
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

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(ppd);

                    ProductPackageOptionDetail packageOptionDetailData = ppd;
                    packageOptionDetailData.setProductId(filterProductOwnerAndBranch.getBranchProductId());
                    packageOptionDetailData.setProductPackageOptionId(y.getBranchProductPackageOptionId());
                 
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

                //set all the product add on under it

                List<ProductAddOnGroup> getListProductAddonGroupByProductById= productAddOnGroupService.listOfProductAddsOnGroup(productId);
                
                if(getListProductAddonGroupByProductById.size()>0){

                    for(ProductAddOnGroup pag : getListProductAddonGroupByProductById){

                        ProductAddOnGroup productAddonData = pag;
                        productAddonData.setStatus("DELETED");
                        productAddOnGroupService.updateProductAddsOnGroup(productAddonData.getId(),productAddonData);

                    }

                }

                List<ProductAddOn> getListOfProductAddon = productAddOnService.getAllProductByProductId(productId);

                if(getListOfProductAddon.size()>0){

                    for (ProductAddOn pao :getListOfProductAddon){

                        ProductAddOn productAddon = pao;
                        productAddon.setStatus("DELETED");
                        productAddOnService.updateProductAddOn(productAddon.getId(), productAddon);

                    }

                }
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

    
    public void bulkEditCategory(List<StoreCategory> categories){

        for(StoreCategory sc:categories){
            Optional<StoreCategory> optStoreCategory = storeCategoryRepository.findById(sc.getId());
            optStoreCategory.get().setSequenceNumber(sc.getSequenceNumber());
            storeCategoryRepository.save(optStoreCategory.get());

        }
    }

    public void bulkEditProductSequence(List<Product> products){

        for(Product p:products){
            Optional<Product> optPrdocut = productRepository.findById(p.getId());
            optPrdocut.get().setSequenceNumber(p.getSequenceNumber());
            productRepository.save(optPrdocut.get());

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

                //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                entityManager.detach(dataOptProduct);
                //to be add data
                Product data = dataOptProduct;
                data.setStoreId(storeBranchId);

                //if null branchcategoryId then we save it in database
                if(filterCategoryOwner.getBranchCategoryId() == null){

                    Optional<StoreCategory> ownerCategory = storeCategoryRepository.findById(filterCategoryOwner.getStoreOwnerCategoryId());

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(ownerCategory.get());

                    StoreCategory bodyStoreCategory = ownerCategory.get();
                    bodyStoreCategory.setStoreId(storeBranchId);
                 
                    StoreCategory saveStoreCategory = storeCategoryRepository.save(bodyStoreCategory);
                
                    for(CompareStoreCategory csoc:compareStoreOwnerCategory){

                        if(csoc.getStoreOwnerCategoryId().equals(ownerCategory.get().getId())){
                            csoc.setBranchCategoryId(saveStoreCategory.getId());
                        }
                    }
                 
                } else{
                    data.setCategoryId(filterCategoryOwner.getBranchCategoryId());

                }
                
                data.setSeoUrl(subProductUrlDomain+dataOptProduct.getSeoName());
             
                //after we save branch product, then we will use the product id of branch
                Product newlyProductData = productRepository.save(data);
                String branchProductId = newlyProductData.getId();
                
                //get product asset (store owner) then clone it
                List<ProductAsset> productAssets = productAssetRepository.findByProductId(dataOptProduct.getId());
                
                if(productAssets.size() != 0){
                    for(ProductAsset pa : productAssets){
                        
                        //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                        entityManager.detach(pa);

                        ProductAsset productAsseData = pa;
                        productAsseData.setProductId(branchProductId);

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

                        //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                        entityManager.detach(pi);

                        ProductInventory productInventoryData = pi;
                        productInventoryData.setItemCode(pi.getItemCode().replaceAll(dataOptProduct.getId(), branchProductId));
                        productInventoryData.setProductId(branchProductId);

                        productInventoryMainRepository.save(productInventoryData);

                    }
                }

                //get vaiant (store owner) then clone it
                List<ProductVariant> ownerProductVariant = productVariantRepository.findByProductId(dataOptProduct.getId());

                //to keep the value for comparing puprose
                List<CompareVariantIdOwnerAndBranch> compareVariantIdOwnerAndBranch = new ArrayList<>();


                if(ownerProductVariant.size() != 0){

                    for(ProductVariant pv : ownerProductVariant){

                        //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                        entityManager.detach(pv);
                        ProductVariant productVariantData = pv;
                        productVariantData.setProduct(newlyProductData);

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
                        
                        //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                        entityManager.detach(pva);
                        ProductVariantAvailable productVariantAvailableData = pva;
                        productVariantAvailableData.setProductId(branchProductId);
                        productVariantAvailableData.setProductVariantId(filterCompareVariantIdOwnerAndBranch.getBranchVariantId());

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

                        //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                        entityManager.detach(pii);

                        ProductInventoryItemMain piiData = pii;

                        piiData.setItemCode(pii.getItemCode().replaceAll(dataOptProduct.getId(), branchProductId));
                        piiData.setProductVariantAvailableId(filterCompareVariantAvailableIdOwnerAndBranch.getBranchVariantAvailableId());
                        piiData.setProductId(branchProductId);

                        ProductInventoryItemMain saveProductInventoryItemMain = productInventoryItemMainRepository.save(piiData);

                    }

                }

                //get product package option
                List<ProductPackageOption> ownerProductPackageOption = productPackageOptionRepository.findByPackageId(dataOptProduct.getId());

                if(ownerProductPackageOption.size() != 0){

                    for(ProductPackageOption ppo :ownerProductPackageOption){

                        //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                        entityManager.detach(ppo);
                        ProductPackageOption ppoData = ppo;
                        ppoData.setPackageId(branchProductId);

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

                        
                        //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                        entityManager.detach(prodAddonGroup);
                        ProductAddOnGroup prodAddonGroupData = prodAddonGroup;

                        //we only save template group for store branch if selected product has addon related to the template group
                        if(filterTemplateGroupOwner.getBranchTemplateGroupId() == null){

                            AddOnTemplateGroup getDetailsOfStoreOwnerAddonTemplateGroup = addOnTemplateGroupRepository.findById(filterTemplateGroupOwner.getStoreTemplateGroupId()).get();
                            //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                            entityManager.detach(getDetailsOfStoreOwnerAddonTemplateGroup);
                            AddOnTemplateGroup bodyAddOnTemplateGroup = getDetailsOfStoreOwnerAddonTemplateGroup;
                            bodyAddOnTemplateGroup.setStoreId(storeBranchId);
                            
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

                        prodAddonGroupData.setProductId(branchProductId);

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

                    //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                    entityManager.detach(prodAddon);

                    //set new product add on for branch
                    ProductAddOn prodAddonData = prodAddon;
                    prodAddonData.setProductId(branchProductId);
                    prodAddonData.setProductAddonGroupId(filterProductAddonGroupOwner.getBranchProductAddonGroupId());

                    //if null then create template item 
                    if(filterDataTemplateGroup.getCompareTemplateItem().get(0).getBranchTemplateItem() == null){

                        //create first by getting detail of owner
                        AddOnTemplateItem getDetailsOfStoreOwnerAddonTemplateItem = addOnTemplateItemRepository.findById(filterDataTemplateGroup.getCompareTemplateItem().get(0).getStoreTemplateItem()).get();
                        //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                        entityManager.detach(prodAddon);
                        AddOnTemplateItem bodyAddonTemplateItem = getDetailsOfStoreOwnerAddonTemplateItem;
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

        assignSelectedProductPackaeOptionDetails(compareProductPackageOption,storeBranchId,compareStoreOwnerCategory,subProductUrlDomain);
        
    }


    public void assignSelectedProductPackaeOptionDetails(List<CompareProductPackageOption> compareProductPackageOption,String storeBranchId, List<CompareStoreCategory> compareStoreOwnerCategory, String subProductUrlDomain){  


        List<Product> branchProducts = productRepository.findByStoreIdAndStatusNot(storeBranchId,"DELETED");

        if(compareProductPackageOption.size()>0){

            for(CompareProductPackageOption cppo: compareProductPackageOption){

                List<ProductPackageOptionDetail> dataProductPacakageDetails = productPackageOptionDetailRepository.findByProductPackageOptionId(cppo.getOwnerProductPackageOptionId());
    
                if(dataProductPacakageDetails.size() != 0){
    
                    for(ProductPackageOptionDetail ppd :dataProductPacakageDetails ){
    
                        Optional<Product> optOwnerProd = productRepository.findById(ppd.getProductId());
                        Product ownerProduct = optOwnerProd.get();
    
                        Optional<Product> optFilterProductBranch = branchProducts.stream()
                        .filter((Product product) -> product.getName().contains(optOwnerProd.get().getName()))
                        .findFirst();
    
                        
                        entityManager.detach(ppd);

                        ProductPackageOptionDetail packageOptionDetailData = ppd;
    
    
                        //we will create the product first if not exist in branch
                        if(!optFilterProductBranch.isPresent()){
    
                            //to find category owner id then we will use the the branch id for creating branch products
                            CompareStoreCategory filterCategoryOwner = compareStoreOwnerCategory.stream()
                            .filter(category -> category.getStoreOwnerCategoryId().equals(ownerProduct.getCategoryId()))
                            .findFirst().get();
            
                            //to be add data
                            //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                            entityManager.detach(ownerProduct);
                            Product data = ownerProduct;
                    
                            data.setStoreId(storeBranchId);
            
                            //if null branchcategoryId then we save it in database
                            if(filterCategoryOwner.getBranchCategoryId() == null){
            
                                Optional<StoreCategory> ownerCategory = storeCategoryRepository.findById(filterCategoryOwner.getStoreOwnerCategoryId());
                                //Entities which previously referenced ,the detached entity will continue to reference it. Detch x--StoreCategory
                                entityManager.detach(ownerCategory.get());
                                StoreCategory bodyStoreCategory = ownerCategory.get();         
                                bodyStoreCategory.setStoreId(storeBranchId);

                                StoreCategory saveStoreCategory = storeCategoryRepository.save(bodyStoreCategory);
                            
                                for(CompareStoreCategory csoc:compareStoreOwnerCategory){
            
                                    if(csoc.getStoreOwnerCategoryId().equals(ownerCategory.get().getId())){
                                        csoc.setBranchCategoryId(saveStoreCategory.getId());
                                    }
                                }
                             
                            } else{
                                data.setCategoryId(filterCategoryOwner.getBranchCategoryId());
            
                            }
                            
                        
                            data.setSeoUrl(subProductUrlDomain+ownerProduct.getSeoName());
                            
                            //after we save branch product, then we will use the product id of branch
                            Product newlyProductData = productRepository.save(data);
                            String branchProductId = newlyProductData.getId();
                            
                            //get product asset (store owner) then clone it
                            List<ProductAsset> productAssets = productAssetRepository.findByProductId(ownerProduct.getId());
                            
                            if(productAssets.size() != 0){
                                for(ProductAsset pa : productAssets){
                                    
                                    entityManager.detach(pa);

                                    ProductAsset productAsseData = pa;
                                    productAsseData.setProductId(branchProductId);
            
                                    if(pa.getItemCode() != null){
            
                                        //set the itemCode (ownerProductId+{int} -> branchProductId+{int})
                                        productAsseData.setItemCode(pa.getItemCode().replaceAll(ownerProduct.getId(),branchProductId));
            
                                    }
                                    productAssetRepository.save(productAsseData);
                                    
                                }
                            }
            
                            //usually for 
                            List<ProductInventory> ownerProductInventory = productInventoryMainRepository.findByProductId(ownerProduct.getId());
                            
                            if(ownerProductInventory.size() != 0){
            
                                for(ProductInventory pi :ownerProductInventory){
                                    
                                    entityManager.detach(pi);

                                    ProductInventory productInventoryData = pi;
                                    productInventoryData.setItemCode(pi.getItemCode().replaceAll(ownerProduct.getId(), branchProductId));
                                    productInventoryData.setProductId(branchProductId);
            
                                    productInventoryMainRepository.save(productInventoryData);
            
                                }
                            }
    
                            packageOptionDetailData.setProductId(branchProductId);
    
                        
                        } else{
                            
                            packageOptionDetailData.setProductId(optFilterProductBranch.get().getId());

                        }
                        
                        packageOptionDetailData.setProductPackageOptionId(cppo.getBranchProductPackageOptionId());
    
                        productPackageOptionDetailRepository.save(packageOptionDetailData);
                    }
    
              
                } 
            }
        }




    }
   
}


