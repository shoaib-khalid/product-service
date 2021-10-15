/*
 * Copyright (C) 2021 mohsi
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
package com.kalsym.product.service.service;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.product.Product;
import com.kalsym.product.service.model.product.ProductInventory;
import com.kalsym.product.service.model.product.ProductInventoryItem;
import com.kalsym.product.service.model.product.ProductInventoryWithDetails;
import com.kalsym.product.service.model.product.ProductVariant;
import com.kalsym.product.service.model.product.ProductVariantAvailable;
import com.kalsym.product.service.model.product.ProductWithVariants;
import com.kalsym.product.service.repository.ProductInventoryItemRepository;
import com.kalsym.product.service.repository.ProductInventoryRepository;
import com.kalsym.product.service.repository.ProductRepository;
import com.kalsym.product.service.utility.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author mohsi
 */
@Service
public class SaveAllProductDetailsService {

    private Product product;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductInventoryRepository productInventoryRespository;

    @Autowired
    ProductInventoryItemRepository productInventoryItemRepository;

    public Product saveProductVariantsAndVariantAvailables(ProductWithVariants bodyProduct) {
        for (ProductVariant bodyVariant : bodyProduct.getProductVariants()) {
            ProductVariant variant = new ProductVariant();
            variant.setName(bodyVariant.getName());
            variant.setDescription(bodyVariant.getDescription());
            variant.setSequenceNumber(bodyVariant.getSequenceNumber());

            for (ProductVariantAvailable bodyVariantAvailable
                    : bodyVariant.getProductVariantsAvailable()) {
                ProductVariantAvailable variantAvailable = new ProductVariantAvailable();
                variantAvailable.setSequenceNumber(bodyVariantAvailable.getSequenceNumber());
                variantAvailable.setValue(bodyVariantAvailable.getValue());

                variantAvailable.setProductVariant(variant);
                variantAvailable.setProduct(this.product);
                variant.getProductVariantsAvailable().add(variantAvailable);
                this.product.getProductVariantsAvailable().add(variantAvailable);
            }

            variant.setProduct(this.product);
            this.product.getProductVariants().add(variant);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "Product Inventories recieved: " + bodyProduct.getProductInventories());
        Product result = productRepository.save(this.product);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "Product added to store");
        return result;
    }

    public void saveProductInventoriesAndInventoryItems(ProductWithVariants bodyProduct,Product result) {
        for (ProductInventoryWithDetails pi : bodyProduct.getProductInventories()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "Inside product with details loop: " + pi);
            pi.setProductId(result.getId());
            ProductInventory productInventory = new ProductInventory(
                    pi.getItemCode(), pi.getPrice(), pi.getCompareAtprice(), pi.getSKU(), pi.getQuantity(), pi.getProductId());

            productInventoryRespository.save(productInventory);
            for (ProductInventoryItem pii : pi.getProductInventoryItems()) {
                pii.setItemCode(pi.getItemCode());
                pii.setProductId(result.getId());
                pii.setProductVariantAvailableId("095c2887-c107-47af-92af-a7cb5d87b7ef");
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, "Product Inventory Item: " + pii);
                productInventoryItemRepository.save(pii);
            }
        }
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
