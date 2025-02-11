package com.mall.xiaomi.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.mapper.ProductMapper;
import com.mall.xiaomi.pojo.Product;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
                List<Product> list = null; // 定义一个 Product 对象的列表，用于存储查询结果

                Example example = new Example(Product.class); // 创建一个 MyBatis 的查询条件对象，用于构造查询条件
                example.orderBy("productSales").desc(); // 设置排序规则，按照 "productSales" 字段进行降序排序

                example.createCriteria().andEqualTo("categoryId", categoryId); // 创建查询条件，匹配 categoryId

                PageHelper.startPage(0, 8); // 使用 PageHelper 分页，查询前 8 个商品

                try {
                        list = productMapper.selectByExample(example); // 执行查询，返回符合条件的商品列表
                        if (ArrayUtils.isEmpty(list.toArray())) { // 检查结果是否为空，如果为空则抛出自定义异常
                                throw new XmException(ExceptionEnum.GET_PRODUCT_NOT_FOUND);
                        }
                } catch (Exception e) {
                        e.printStackTrace(); // 打印异常堆栈信息
                        throw new XmException(ExceptionEnum.GET_PRODUCT_ERROR); // 抛出自定义异常，表示获取商品出错
                }

                return list; // 返回查询到的商品列表
        }

        public List<Product> getHotProduct() {
                Example example = new Example(Product.class);
                example.orderBy("productSales").desc();

                PageHelper.startPage(0, 8);
                List<Product> list = null;
                try {
                        list = productMapper.selectByExample(example);
                        if (ArrayUtils.isEmpty(list.toArray())) {
                                throw new XmException(ExceptionEnum.GET_PRODUCT_NOT_FOUND);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new XmException(ExceptionEnum.GET_PRODUCT_ERROR);
                }
                return list;
        }

        public Product getProductById(String productId) {
                Product product = null;
                try {
                        product = productMapper.selectByPrimaryKey(productId);
                        if (product == null) {
                                throw new XmException(ExceptionEnum.GET_PRODUCT_NOT_FOUND);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new XmException(ExceptionEnum.GET_PRODUCT_ERROR);
                }
                return product;
        }

        public PageInfo<Product> getProductByPage(String currentPage, String pageSize, String categoryId) {
                List<Product> list = null;
                PageHelper.startPage(Integer.parseInt(currentPage) - 1, Integer.parseInt(pageSize), true);
                if (categoryId.equals("0")) { // 为0，代表分页查询所有商品
                        list = productMapper.selectAll();
                } else {
                        // 分类分页查询商品
                        Product product = new Product();
                        product.setCategoryId(Integer.parseInt(categoryId));
                        list = productMapper.select(product);
                }
                PageInfo<Product> pageInfo = new PageInfo<Product>(list);
                return pageInfo;
        }
}
