package com.mall.xiaomi.service;

import com.mall.xiaomi.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:21
 * @Description:
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

}
