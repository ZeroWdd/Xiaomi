package com.mall.xiaomi.mapper;

import com.mall.xiaomi.pojo.SeckillMessageRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeckillMessageRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SeckillMessageRecord record);

    int insertSelective(SeckillMessageRecord record);

    SeckillMessageRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SeckillMessageRecord record);

    int updateByPrimaryKey(SeckillMessageRecord record);

    com.mall.xiaomi.pojo.SeckillMessageRecord findByMessageId(@Param("correlationId") String correlationId);

    int updateByMessageId(@Param("record") SeckillMessageRecord record);
}
