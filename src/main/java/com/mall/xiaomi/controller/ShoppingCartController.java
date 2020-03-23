package com.mall.xiaomi.controller;

import com.mall.xiaomi.pojo.ShoppingCart;
import com.mall.xiaomi.service.ShoppingCartService;
import com.mall.xiaomi.util.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:27
 * @Description:
 */
@RestController
@RequestMapping("/cart")
public class ShoppingCartController{

    @Autowired
    private ResultMessage resultMessage;
    @Autowired
    private ShoppingCartService cartService;

    @GetMapping("/user/{userId}")
    public ResultMessage cart(@PathVariable String userId) {
        List<ShoppingCart> carts = cartService.getCartByUserId(userId);
        resultMessage.success("001", carts);
        return resultMessage;
    }
}
