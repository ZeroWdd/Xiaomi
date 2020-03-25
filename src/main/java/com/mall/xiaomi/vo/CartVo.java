package com.mall.xiaomi.vo;

import lombok.Data;

/**
 * @Auther: wdd
 * @Date: 2020-03-25 12:37
 * @Description:
 */
@Data
public class CartVo {

    private Integer id;

    private Integer productId;

    private String productName;

    private String productImg;

    private Double price;

    private Integer num;

    private Integer maxNum;

    private boolean check;
}
