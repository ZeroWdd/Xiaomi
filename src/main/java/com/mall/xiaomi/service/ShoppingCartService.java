package com.mall.xiaomi.service;

import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.mapper.ShoppingCartMapper;
import com.mall.xiaomi.pojo.ShoppingCart;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:22
 * @Description:
 */
@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartMapper cartMapper;

    public List<ShoppingCart> getCartByUserId(String userId) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(Integer.parseInt(userId));
        List<ShoppingCart> list = null;
        try {
            list = cartMapper.select(cart);
        } catch (Exception e) {
            e.printStackTrace();
            throw new XmException(ExceptionEnum.GET_CART_ERROR);
        }
        return list;
    }
}
