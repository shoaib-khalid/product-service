package com.kalsym.product.service.model.store;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.kalsym.product.service.enums.StoreAssetType;
import com.kalsym.product.service.model.ItemDiscount;
import javax.persistence.Transient;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "store_assets")
@NoArgsConstructor
public class StoreAssets implements Serializable {

    @Id
    private String id;
    
    private String storeId;

    private String assetUrl;
    private String assetDescription;
    private StoreAssetType assetType;
    
    @Transient 
    private MultipartFile assetFile;
    
}
