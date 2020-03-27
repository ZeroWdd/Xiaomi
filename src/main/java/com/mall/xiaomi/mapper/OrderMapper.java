package com.mall.xiaomi.mapper;

import com.mall.xiaomi.pojo.Order;
import com.mall.xiaomi.vo.OrderVo;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderMapper extends Mapper<Order> {

    @Select("select `order`.*, product.product_name as productName, product.product_picture as productPicture " +
            "from `order`, product where `order`.product_id = product.product_id and `order`.user_id = #{userId}")
    List<OrderVo> getOrderVoByUserId(Integer userId);
}