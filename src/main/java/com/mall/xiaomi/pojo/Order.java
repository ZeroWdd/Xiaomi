package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "JDBC")
    private Integer id;

    private String orderId;

    private Integer userId;

    private Integer productId;

    private Integer productNum;

    private Double productPrice;

    private Long orderTime;

}