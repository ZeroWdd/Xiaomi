package com.mall.xiaomi.service;

import com.github.pagehelper.PageHelper;
import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.mapper.ProductMapper;
import com.mall.xiaomi.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:21
 * @Description:
 */
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public List<Product> getProductByCategoryId(Integer categoryId) {
        List<Product> list = null;
        Product product = new Product();
        product.setCategoryId(categoryId);
        try {
            list = productMapper.select(product);
            if (list.isEmpty()) {
                throw new XmException(ExceptionEnum.GET_PRODUCT_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new XmException(ExceptionEnum.GET_PRODUCT_ERROR);
        }
        return list;
    }

    public List<Product> getHotProduct() {
        Example example = new Example(Product.class);
        example.orderBy("productSales").desc();

        PageHelper.startPage(0, 7);
        List<Product> list = productMapper.selectByExample(example);

        return list;
    }
}
