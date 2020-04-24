package com.mall.xiaomi.mq;

import com.mall.xiaomi.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Auther: wdd
 * @Date: 2020-04-24 9:30
 * @Description:
 */
@Component
public class SeckillOrderQueue {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "seckill_order")
    public void insertOrder(Map map){

        String seckillId = (String) map.get("seckillId");
        String userId = (String) map.get("userId");

        orderService.addSeckillOrder(seckillId, userId);
    }
}
