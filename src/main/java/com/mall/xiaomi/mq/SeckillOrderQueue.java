package com.mall.xiaomi.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.xiaomi.mapper.SeckillMessageRecordMapper;
import com.mall.xiaomi.pojo.SeckillMessageRecord;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wdd
 * @Date: 2020-04-24 9:30
 * @Description:
 */
@Component
public class SeckillOrderQueue {

        private static final int MAX_RETRY_COUNT = 3;

        private static final String RETRY_COUNT_KEY_PREFIX = "retryCount:";
        @Autowired
        private OrderService orderService;
        @Autowired
        private SeckillProductService seckillProductService;
        @Autowired
        StringRedisTemplate stringRedisTemplate;

        @Autowired
        SeckillMessageRecordMapper seckillMessageRecordMapper;

        @Autowired
        ObjectMapper objectMapper;

        @RabbitListener(queues = "seckill_order", containerFactory = "rabbitListenerContainerFactory")
        public void insertOrder(Map map, Channel channel, Message message) {
                String correlationId = message.getMessageProperties().getCorrelationId();
                String redisKey = RedisKey.SECKILL_RABBITMQ_ID + correlationId;

                // 幂等性检查
                // 从数据库中查找是否已存在该消息的记录
                SeckillMessageRecord record = seckillMessageRecordMapper.findByMessageId(correlationId);
                if (record != null && ("PROCESSED".equals(record.getStatus()) || "FAILED".equals(record.getStatus()))) {

                        System.out.println("Message with ID " + correlationId + " has already been processed.");
                        try {
                                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        return; // 已经处理过的消息直接返回
                }

                if (record == null) {
                        // 如果记录不存在，创建一条新记录
                        record = new SeckillMessageRecord();
                        record.setMessageId(correlationId);
                        record.setUserId((String) map.get("userId"));
                        record.setSeckillId((String) map.get("seckillId"));
                        record.setCreatedAt(new Date());
                        seckillMessageRecordMapper.insert(record);
                }

                try {
                        String seckillId = (String) map.get("seckillId");
                        String userId = (String) map.get("userId");

                        orderService.addSeckillOrder(seckillId, userId);

                        // 消息成功处理，更新状态
                        record.setStatus("PROCESSED");

                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } catch (Exception e) {
                        // 增加重试次数
                        record.setRetryCount(
                                record.getRetryCount() == null ? 1 : record.getRetryCount() + 1
                        );

                        record.setErrorMessage(e.getMessage());

                        if (record.getRetryCount() >= MAX_RETRY_COUNT) {
                                // 达到最大重试次数，将消息标记为失败
                                record.setStatus("FAILED");
                                try {
                                        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                                } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                }
                        } else {
                                // 保存重试状态，并重新入队
                                seckillMessageRecordMapper.updateByMessageId(record);
                                try {
                                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                                } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                }
                        }
                } finally {
                        record.setUpdatedAt(new Date());
                        seckillMessageRecordMapper.updateByMessageId(record);
                }
        }

        @RabbitListener(queues = "seckill_dead_letter_queue")
        public void processDeadLetterMessage(Map<String, Object> map) {
                String seckillId = (String) map.get("seckillId");
                String userId = (String) map.get("userId");
                String messageId = seckillId + ":" + userId;
                // 查询数据库中是否有对应的消息记录
                Optional<SeckillMessageRecord> optionalRecord = Optional.ofNullable(seckillMessageRecordMapper.findByMessageId(messageId));

                if (optionalRecord.isPresent()) {

                        System.out.println("Dead letter message processed and saved: " + messageId);
                } else {
                        System.out.println("Dead letter message not found in records: " + messageId);
                }
        }

}
