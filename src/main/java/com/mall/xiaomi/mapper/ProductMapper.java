package com.mall.xiaomi.mapper;

import com.mall.xiaomi.pojo.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductMapper extends Mapper<Product> {

        @Select("select product_id from product")
        List<Integer> selectIds();

        int updateStockByIdAndVersion(@Param("productId") Integer productId, @Param("saleNum") Integer saleNum, @Param("currentVersion") int currentVersion);
}
