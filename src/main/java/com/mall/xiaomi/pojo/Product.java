package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "JDBC")
    private Integer productId;

    private String productName;

    private Integer categoryId;

    private String productTitle;

    private String productPicture;

    private Double productPrice;

    private Double productSellingPrice;

    private Integer productNum;

    private Integer productSales;

    private String productIntro;

}