package com.mall.xiaomi.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GoodsCart {

    private Integer id;

    private Integer goodsId;

    private Integer buyCount;

    private Date addDate;

    private Double subtotal;

    private Integer consumerId;

    private List<GoodsImages> goodsImages;

    public GoodsCart(Integer goodsId, Integer buyCount, Date addDate, Double subtotal, Integer consumerId) {
        this.goodsId = goodsId;
        this.buyCount = buyCount;
        this.addDate = addDate;
        this.subtotal = subtotal;
        this.consumerId = consumerId;
    }

}