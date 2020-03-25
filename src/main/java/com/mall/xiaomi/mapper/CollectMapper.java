package com.mall.xiaomi.mapper;

import com.mall.xiaomi.pojo.Collect;
import com.mall.xiaomi.pojo.Product;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CollectMapper extends Mapper<Collect> {

    @Select("select product.* from product, collect where collect.user_id = #{userId} and collect.product_id = product.product_id")
    List<Product> getCollect(String userId);
}