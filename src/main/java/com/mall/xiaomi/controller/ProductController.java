package com.mall.xiaomi.controller;

import com.mall.xiaomi.pojo.Product;
import com.mall.xiaomi.service.ProductService;
import com.mall.xiaomi.util.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:26
 * @Description:
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ResultMessage resultMessage;
    @Autowired
    private ProductService productService;

    @GetMapping("/category/{categoryId}")
    public ResultMessage getProductByCategoryId(@PathVariable Integer categoryId) {
        List<Product> list = productService.getProductByCategoryId(categoryId);
        resultMessage.success("001", list);
        return resultMessage;

    }

    @GetMapping("/category/hot")
    public ResultMessage getHotProduct() {
        List<Product> list = productService.getHotProduct();
        resultMessage.success("001", list);
        return resultMessage;

    }

}
