package com.kalsym.product.service.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 7cu
 */
@Entity
@Getter
@Setter
@Table(name = "store")
@ToString
public class Store implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String name;

    private String city;

    private String address;

    private String clientId;

    private String verticalCode;

    public void update(Store store) {

        if (null != store.getCity()) {
            city = store.getCity();
        }

        if (null != store.getName()) {
            name = store.getName();
        }

        if (null != store.getAddress()) {
            address = store.getAddress();
        }

        if (null != store.getClientId()) {
            clientId = store.getClientId();
        }
        if (null != store.getVerticalCode()) {
            verticalCode = store.getVerticalCode();
        }

    }
}
