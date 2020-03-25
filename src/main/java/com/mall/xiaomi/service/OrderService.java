package com.mall.xiaomi.service;

import com.mall.xiaomi.mapper.OrderMapper;
import com.mall.xiaomi.pojo.Order;
import com.mall.xiaomi.util.IdWorker;
import com.mall.xiaomi.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:21
 * @Description:
 */
@Service
public class OrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderMapper orderMapper;

    public void addOrder(CartVo cartVo, Integer userId) {
        Order order = new Order();
        order.setOrderId(idWorker.nextId() + "");
        order.setOrderTime(new Date().getTime());
        order.setProductNum(cartVo.getNum());
        order.setProductId(cartVo.getProductId());
        order.setProductPrice(cartVo.getPrice());
        order.setUserId(userId);
        orderMapper.insert(order);
    }
}
