package com.mall.xiaomi.controller;

import com.mall.xiaomi.service.OrderService;
import com.mall.xiaomi.util.ResultMessage;
import com.mall.xiaomi.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:25
 * @Description:
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ResultMessage resultMessage;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;

    @PostMapping("")
    public ResultMessage addOrder(@RequestBody CartVo cartVo, @CookieValue("XM_TOKEN") String cookie) {
        // 先判断cookie是否存在，和redis校验
        Integer userId = (Integer) redisTemplate.opsForHash().get(cookie, "userId");
        orderService.addOrder(cartVo, userId);
        resultMessage.success("001", "下单成功");
        return resultMessage;
    }

}
