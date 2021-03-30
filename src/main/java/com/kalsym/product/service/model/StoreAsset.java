package com.kalsym.product.service.model;

import com.kalsym.product.service.model.product.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

}
