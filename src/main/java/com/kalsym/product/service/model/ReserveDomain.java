package com.kalsym.product.service.model;

import javax.persistence.Entity;

import javax.persistence.Id;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "reserved_domain")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)

public class ReserveDomain {
 
    @Id
    private Integer id;

    private String domain;

}

