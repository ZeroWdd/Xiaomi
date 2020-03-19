package com.mall.xiaomi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 16:41
 * @Description:
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

    GET_CAROUSEL_ERROR(002, "轮播图查询失败"),
    GET_CAROUSEL_NOT_FOUND(002, "轮播图为空"),

    GET_PRODUCT_ERROR(002, "商品查询失败"),
    GET_PRODUCT_NOT_FOUND(002, "商品为空"),

    ;
    private int code;
    private String msg;
}
