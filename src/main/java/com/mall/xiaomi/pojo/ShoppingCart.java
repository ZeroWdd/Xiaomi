package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "shopping_cart")
public class ShoppingCart {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
        private Integer id;

        private Integer userId;

        private Integer productId;

        private Integer num;

        private Integer version; // 版本号，用于乐观锁控制
}
