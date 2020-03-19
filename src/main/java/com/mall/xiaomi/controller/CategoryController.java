package com.mall.xiaomi.controller;

import com.mall.xiaomi.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:25
 * @Description:
 */
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

}
