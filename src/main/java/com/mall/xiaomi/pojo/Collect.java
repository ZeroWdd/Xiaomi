package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "collect")
public class Collect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "JDBC")
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Long collectTime;

}