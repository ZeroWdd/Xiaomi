package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Auther: wdd
 * @Date: 2020-04-22 20:57
 * @Description:
 */
@Data
@Table(name = "seckill_time")
public class SeckillTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "JDBC")
    private Integer timeId;

    private Long startTime;

    private Long endTime;

}
