package com.mall.xiaomi.mapper;

import com.mall.xiaomi.pojo.SeckillTime;
import com.mall.xiaomi.vo.SeckillProductVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: wdd
 * @Date: 2020-04-22 20:58
 * @Description:
 */
public interface SeckillTimeMapper extends Mapper<SeckillTime> {

    @Select("select * from seckill_time where end_time > #{time} limit 6")
    List<SeckillTime> getTime(long time);

    @Delete("delete from seckill_time")
    void deleteAll();

    @Select("select endTime from seckill_time where time_id = #{timeId}")
    Long getEndTime(Integer timeId);
}
