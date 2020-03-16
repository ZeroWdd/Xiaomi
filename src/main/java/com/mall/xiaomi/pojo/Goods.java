package com.mall.xiaomi.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Goods {

    private Integer id;

    private String name;

    private Double price;

    private Integer stock;

    private Integer goodsTypeId;

    private String remark;

    private List<GoodsImages> goodsImages;

}