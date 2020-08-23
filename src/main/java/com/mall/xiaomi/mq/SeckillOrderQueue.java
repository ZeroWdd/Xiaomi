package com.mall.xiaomi.mq;

import com.mall.xiaomi.service.OrderService;
import com.mall.xiaomi.service.SeckillProductService;
import com.mall.xiaomi.util.RedisKey;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wdd
 * @Date: 2020-04-24 9:30
 * @Description:
 */
@Component
public class SeckillOrderQueue {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = "seckill_order")
    public void insertOrder(Map map, Channel channel, Message message){

        // 查看id，保证幂等性
        String correlationId = message.getMessageProperties().getCorrelationId();
        if (!stringRedisTemplate.hasKey(RedisKey.SECKILL_RABBITMQ_ID + correlationId)) {
            // redis中存在，表明此条消息已消费，请勿重复消费
            return;
        }
        String seckillId = (String) map.get("seckillId");
        String userId = (String) map.get("userId");
        // 存入redis，因为只需要判断是否存在，因此value为多少无所谓
        stringRedisTemplate.opsForValue().set(RedisKey.SECKILL_RABBITMQ_ID + correlationId, "1");
        Long seckillEndTime = seckillProductService.getEndTime(seckillId);
        stringRedisTemplate.expire(RedisKey.SECKILL_RABBITMQ_ID + correlationId, seckillEndTime - new Date().getTime(), TimeUnit.SECONDS); // 设置过期时间

        try {
            orderService.addSeckillOrder(seckillId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                stringRedisTemplate.delete(RedisKey.SECKILL_RABBITMQ_ID + correlationId);
                // 将该消息放入队列尾部，尝试再次消费
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
