package com.kalsym.product.service.service;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Sarosh
 */
@Service
public class FileStorageService {

    @Value("${product.assets.location:/var/www/html/symplified-assets/product-assets}")
    private String productAssetStorageLocation;

    @Value("${store.assets.location:/var/www/html/symplified-assets/store-assets}")
    private String storeAssetStorageLocation;

    private Path productAssetStorageaPath;

    private Path storeAssetStorageaPath;

    public String saveProductAsset(MultipartFile file, String name) {
        String logprefix = "saveProductAsset";

        try {
            this.productAssetStorageaPath = Paths.get(productAssetStorageLocation).toAbsolutePath().normalize();
            Files.createDirectories(this.productAssetStorageaPath);
        } catch (IOException e) {
            Logger.application.error(ProductServiceApplication.VERSION, logprefix, "IOException: ", e);
        }

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.productAssetStorageaPath.resolve(name);
            long result = Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "result: " + result);

            return fileName;
        } catch (IOException e) {
            Logger.application.error(ProductServiceApplication.VERSION, logprefix, "could not store file: ", e);
            return null;
        }

    }

    public String saveStoreAsset(MultipartFile file, String name) {
        String logprefix = "saveStoreAsset";

        try {
            this.storeAssetStorageaPath = Paths.get(storeAssetStorageLocation).toAbsolutePath().normalize();
            Files.createDirectories(this.storeAssetStorageaPath);
        } catch (IOException e) {
            Logger.application.error(ProductServiceApplication.VERSION, logprefix, "IOException: ", e);
        }

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.storeAssetStorageaPath.resolve(name);
            long result = Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            Logger.application.info(ProductServiceApplication.VERSION, logprefix, "result: " + result);

            return fileName;
        } catch (IOException e) {
            Logger.application.error(ProductServiceApplication.VERSION, logprefix, "could not store file: ", e);
            return null;
        }

    }
    
        public String getFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}
