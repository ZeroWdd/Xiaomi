package com.mall.xiaomi.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class GoodsOrder {

    private String id;

    private Date orderDate;

    private Integer consumerId;

    private String status;

    private Integer goodsShippingAddressId;

}