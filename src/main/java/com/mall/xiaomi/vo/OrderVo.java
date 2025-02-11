package com.mall.xiaomi.vo;

import com.mall.xiaomi.pojo.Order;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: wdd
 * @Date: 2020-03-27 16:29
 * @Description:
 */
@Data
public class OrderVo extends Order {

        private String productName;

        private String productPicture;

        private Long orderTime;

}
