package com.kalsym.product.service.model.product;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.kalsym.product.service.model.request.AddOnTemplateItemRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "addon_template_item")
public class AddOnTemplateItem {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String groupId;

    private String name;

    private Double price;

    private Double dineInPrice;

    public static AddOnTemplateItem castReference(AddOnTemplateItemRequest reqBody){

        AddOnTemplateItem body = new AddOnTemplateItem();
        //set the id for update data
        if(reqBody.getId() != null){

            body.setId(reqBody.getId());

        }
        body.setGroupId(reqBody.getGroupId());
        body.setName(reqBody.getName());

        // if new client for delivery, we auto set the dine in price reduce 15%
        if (reqBody.getDineInPrice()==null) {
            body.setDineInPrice(reqBody.getPrice()*0.85);
        } else{
            body.setDineInPrice(reqBody.getDineInPrice());
        }

        // if new client for dinein we auto set for delivery price  Increase 17.5%
        if (reqBody.getPrice()==null) {
            body.setPrice(reqBody.getDineInPrice()*1.175);
        } else{
            body.setPrice(reqBody.getPrice());

        }

        return body;
    }
}
