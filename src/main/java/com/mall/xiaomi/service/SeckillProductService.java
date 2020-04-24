package com.mall.xiaomi.service;

import com.github.pagehelper.PageInfo;
import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.mapper.SeckillProductMapper;
import com.mall.xiaomi.mapper.SeckillTimeMapper;
import com.mall.xiaomi.pojo.SeckillProduct;
import com.mall.xiaomi.pojo.SeckillTime;
import com.mall.xiaomi.util.BeanUtil;
import com.mall.xiaomi.vo.SeckillProductVo;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wdd
 * @Date: 2020-03-28 20:01
 * @Description:
 */
@Service
public class SeckillProductService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SeckillProductMapper seckillProductMapper;
    @Autowired
    private SeckillTimeMapper seckillTimeMapper;

    private final static String SECKILL_PRODUCT_LIST = "seckill:product:list:";
    private final static String SECKILL_PRODUCT = "seckill:product:id:";
    private final static String SECKILL_PRODUCT_STOCK = "seckill:product:stock:id:";
    private final static String SECKILL_PRODUCT_USER_LIST = "seckill:product:user:list";

    private HashMap<String, Boolean> localOverMap = new HashMap<>();

    @Transactional
    public List<SeckillProductVo> getProduct(String timeId) {

        // 先查看缓存，是否有列表
        List<SeckillProductVo> seckillProductVos = redisTemplate.opsForList().range(SECKILL_PRODUCT_LIST + timeId, 0, -1);
        if (ArrayUtils.isNotEmpty(seckillProductVos.toArray())) {
            return seckillProductVos;
        }
        // 缓存没有，再从数据库中获取，添加到缓存
        seckillProductVos = seckillProductMapper.getSeckillProductVos(timeId, new Date().getTime());
        if (ArrayUtils.isNotEmpty(seckillProductVos.toArray())) {
            redisTemplate.opsForList().leftPushAll(SECKILL_PRODUCT_LIST + timeId, seckillProductVos);
            // 设置过期时间
            long l = seckillProductVos.get(0).getEndTime() - new Date().getTime();
            redisTemplate.expire(SECKILL_PRODUCT_LIST + timeId, l, TimeUnit.MILLISECONDS);
        }else {
            // 秒杀商品过期或不存在
            throw new XmException(ExceptionEnum.GET_SECKILL_NOT_FOUND);
        }
        return seckillProductVos;
    }

    public void addSeckillProduct(SeckillProduct seckillProduct) {
        // TODO: 仿添加秒杀商品
        Date time = getDate();
        long startTime = time.getTime()/1000*1000 + 1000 * 60 * 60;
        long endTime = startTime + 1000 * 60 * 60;
        SeckillTime seckillTime = new SeckillTime();
        seckillTime.setStartTime(startTime);
        seckillTime.setEndTime(endTime);
        // 先查看是否有该时间段
        SeckillTime one = seckillTimeMapper.selectOne(seckillTime);
        if (one == null) {
            seckillTimeMapper.insert(seckillTime);
            seckillProduct.setTimeId(seckillTime.getTimeId());
        }else {
            seckillProduct.setTimeId(one.getTimeId());
        }
        seckillProductMapper.insert(seckillProduct);
    }

    /**
     * 获取当前时间的整点
     * @return
     */
    private Date getDate() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        return ca.getTime();
    }

    public List<SeckillTime> getTime() {
        // 获取当前时间及往后7个时间段, 总共8个
        Date time = getDate();
        List<SeckillTime> seckillTimes = seckillTimeMapper.getTime(time.getTime()/1000*1000);
        return seckillTimes;
    }

    public SeckillProductVo getSeckill(String seckillId) {
        // 从缓存中查询
        Map map = redisTemplate.opsForHash().entries(SECKILL_PRODUCT + seckillId);
        if (!map.isEmpty()) {
            map.size();
            SeckillProductVo seckillProductVo = null;
            try {
                seckillProductVo = BeanUtil.map2bean(map, SeckillProductVo.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return seckillProductVo;
        }
        // 从数据库中查询
        SeckillProductVo seckillProductVo = seckillProductMapper.getSeckill(seckillId);
        if (seckillProductVo != null) {
            try {
                redisTemplate.opsForHash().putAll(SECKILL_PRODUCT + seckillId, BeanUtil.bean2map(seckillProductVo));
                redisTemplate.expire(SECKILL_PRODUCT + seckillId, seckillProductVo.getEndTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
                // 将库存单独存入一个key中
                if (stringRedisTemplate.opsForValue().get(SECKILL_PRODUCT_STOCK + seckillId) == null) {
                    stringRedisTemplate.opsForValue().set(SECKILL_PRODUCT_STOCK + seckillId, seckillProductVo.getSeckillStock() + "",seckillProductVo.getEndTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return seckillProductVo;
        }
        return null;
    }

    /**
     * 秒杀
     * @param seckillId
     */
    @Transactional
    public void seckillProduct(String seckillId, Integer userId) {
        if (localOverMap.get(seckillId) != null && localOverMap.get(seckillId)) {
            // 售空
            throw new XmException(ExceptionEnum.GET_SECKILL_IS_OVER);
        }
        // 判断秒杀是否开始, 防止路径暴露被刷
        Map m = redisTemplate.opsForHash().entries(SECKILL_PRODUCT + seckillId);
        if (!m.isEmpty()) {
            SeckillProductVo seckillProductVo = null;
            try {
                seckillProductVo = BeanUtil.map2bean(m, SeckillProductVo.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 秒杀开始时间
            Long startTime = seckillProductVo.getStartTime();

            if (startTime > new Date().getTime()) {
                throw new XmException(ExceptionEnum.GET_SECKILL_IS_NOT_START);
            }
        }

        // 判断是否已经秒杀到了，避免一个账户秒杀多个商品
        List<String> list = redisTemplate.opsForList().range(SECKILL_PRODUCT_USER_LIST + seckillId, 0, -1);
        if (list.contains(String.valueOf(userId))) {
            throw new XmException(ExceptionEnum.GET_SECKILL_IS_REUSE);
        }

        // 预减库存：从缓存中减去库存
        // 利用redis中的方法，减去库存，返回值为减去1之后的值
        if (stringRedisTemplate.opsForValue().decrement(SECKILL_PRODUCT_STOCK + seckillId) < 0) {
            // 设置内存标记
            localOverMap.put(seckillId, true);
            // 秒杀完成，库存为空
            throw new XmException(ExceptionEnum.GET_SECKILL_IS_OVER);
        }

        // 使用RabbitMQ异步传输
        HashMap<String, String> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("userId", userId.toString());
        try {
            rabbitTemplate.convertAndSend("seckill_order", map);
        } catch (AmqpException e) {
            e.printStackTrace();
            stringRedisTemplate.opsForValue().increment(SECKILL_PRODUCT_STOCK + seckillId);
        }

    }
}
