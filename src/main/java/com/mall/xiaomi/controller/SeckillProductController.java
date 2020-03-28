package com.mall.xiaomi.controller;

import com.mall.xiaomi.service.SeckillProductService;
import com.mall.xiaomi.util.ResultMessage;
import com.mall.xiaomi.vo.SeckillProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: wdd
 * @Date: 2020-03-28 19:58
 * @Description:
 */
@RestController
@RequestMapping("/seckill/product")
public class SeckillProductController {

    @Autowired
    private ResultMessage resultMessage;
    @Autowired
    private SeckillProductService seckillProductService;

    /**
     * 根据时间id获取对应时间的秒杀商品列表
     * @param timeId
     * @return
     */
    @GetMapping("/time/{timeId}")
    public ResultMessage getProduct(@PathVariable String timeId) {
        List<SeckillProductVo> seckillProductVos = seckillProductService.getProduct(timeId);
        resultMessage.success("001", seckillProductVos);
        return resultMessage;
    }

}
