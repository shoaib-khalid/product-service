package com.kalsym.product.service.model.product;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.kalsym.product.service.model.request.AddOnGroupTemplateRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "addon_template_group")
public class AddOnTemplateGroup {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String storeId;

    private String title;

    public static AddOnTemplateGroup castReference(AddOnGroupTemplateRequest req){

        AddOnTemplateGroup body = new AddOnTemplateGroup();
        //set the id for update data
        if(req.getId() != null){

            body.setId(req.getId());

        }
        body.setStoreId(req.getStoreId());
        body.setTitle(req.getTitle());

        return body;
    }
}
