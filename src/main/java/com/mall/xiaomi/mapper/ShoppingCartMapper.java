package com.mall.xiaomi.mapper;

import com.mall.xiaomi.pojo.ShoppingCart;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ShoppingCartMapper extends Mapper<ShoppingCart> {

        int updateCartByIdAndVersion(@Param("cart") ShoppingCart cart);
}
