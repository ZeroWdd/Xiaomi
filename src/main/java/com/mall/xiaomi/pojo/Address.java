package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;

    private Integer userId;

    private String name;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detail;

    private Integer isDefault;
}
