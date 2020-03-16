package com.mall.xiaomi.pojo;

import lombok.Data;

@Data
public class GoodsOrderItem {

    private String id;

    private Integer goodsId;

    private String goodsImg;

    private Double goodsPrice;

    private Integer goodsCount;

    private Double goodsSubtotal;

    private String goodsOrderId;

}