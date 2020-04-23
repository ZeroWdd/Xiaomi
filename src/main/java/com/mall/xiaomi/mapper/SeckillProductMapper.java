package com.mall.xiaomi.mapper;

import com.mall.xiaomi.pojo.SeckillProduct;
import com.mall.xiaomi.vo.SeckillProductVo;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: wdd
 * @Date: 2020-03-28 20:10
 * @Description:
 */
public interface SeckillProductMapper extends Mapper<SeckillProduct> {

    @Select("select seckill_time.start_time, seckill_time.end_time, seckill_product.*, product.product_name, product.product_price, product.product_picture from seckill_product ,product, seckill_time where seckill_product.time_id = seckill_time.time_id and seckill_product.product_id = product.product_id and seckill_product.time_id = #{timeId} and seckill_time.end_time > #{time}")
    List<SeckillProductVo> getSeckillProductVos(String timeId, Long time);

    @Select("select seckill_time.start_time, seckill_time.end_time, seckill_product.*, product.product_name, product.product_price, product.product_picture from seckill_product ,product, seckill_time where seckill_product.time_id = seckill_time.time_id and seckill_product.product_id = product.product_id and seckill_product.seckill_id = #{seckillId}")
    SeckillProductVo getSeckill(String seckillId);
}
