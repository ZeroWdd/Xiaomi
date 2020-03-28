package com.mall.xiaomi.service;

import com.mall.xiaomi.mapper.SeckillProductMapper;
import com.mall.xiaomi.vo.SeckillProductVo;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private SeckillProductMapper seckillProductMapper;

    private final static String SECKILL_PRODUCT_LIST = "seckill:product:list:";

    public List<SeckillProductVo> getProduct(String timeId) {
        // 先查看缓存，是否有列表
        List<SeckillProductVo> seckillProductVos = redisTemplate.opsForList().range(SECKILL_PRODUCT_LIST + timeId, 0, -1);
        if (ArrayUtils.isNotEmpty(seckillProductVos.toArray())) {
            return seckillProductVos;
        }
        // 缓存没有，再从数据库中获取，添加到缓存

        return null;
    }
}
