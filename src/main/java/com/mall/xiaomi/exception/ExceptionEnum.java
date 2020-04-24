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


    GET_PRODUCT_PICTURE_ERROR(002, "商品图片查询失败"),
    GET_PRODUCT_PICTURE_NOT_FOUND(002, "商品图片为空"),


    GET_CATEGORY_ERROR(002, "分类查询异常"),
    GET_CATEGORY_NOT_FOUND(002, "分类查询为空"),

    GET_USER_NOT_FOUND(002, "用户名或密码错误"),
    SAVE_USER_REUSE(002, "用户名已存在"),
    SAVE_USER_ERROR(002, "注册用户失败"),



    GET_CART_ERROR(002, "购物车异常"),
    GET_CART_NOT_FOUND(002, "购物车为空"),


    SAVE_COLLECT_ERROR(002, "收藏失败"),
    SAVE_COLLECT_REUSE(002, "已收藏，请勿重复收藏"),
    GET_COLLECT_NOT_FOUND(002, "尚无商品收藏"),
    GET_COLLECT_ERROR(002, "获取收藏失败"),
    DELETE_COLLECT_ERROR(002, "删除收藏失败"),



    ADD_CART_NUM_UPPER(003, "该商品购物车达到上限"),
    UPDATE_CART_ERROR(003, "商品数量修改失败"),
    DELETE_CART_ERROR(003, "商品删除失败"),


    ADD_ORDER_ERROR(002, "生成订单失败"),
    GET_ORDER_NOT_FOUND(002, "订单为空"),
    GET_ORDER_ERROR(002, "查询订单失败"),

    GET_SECKILL_NOT_FOUND(002, "尚无秒杀商品"),
    GET_SECKILL_IS_OVER(002, "秒杀商品售罄"),
    GET_SECKILL_IS_REUSE(002, "秒杀重复"),
    GET_SECKILL_IS_NOT_START(002, "秒杀尚未开始"),

    ;
    private int code;
    private String msg;
}
