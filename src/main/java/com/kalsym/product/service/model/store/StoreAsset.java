package com.kalsym.product.service.model.store;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "store_asset")
@NoArgsConstructor
public class StoreAsset implements Serializable {

    @Id
    private String storeId;

    private String logoUrl;
    private String bannerUrl;
    private String bannerMobileUrl;
}
