package com.kalsym.product.service.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "promo_text")
@Getter
@Setter
@ToString
public class PromoText implements Serializable{
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String eventId;

    private String displayText;

}
