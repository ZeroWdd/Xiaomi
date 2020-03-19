package com.mall.xiaomi.controller;

import com.mall.xiaomi.pojo.Carousel;
import com.mall.xiaomi.service.CarouselService;
import com.mall.xiaomi.util.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:24
 * @Description:
 */
@RestController
public class CarouselController {

    @Autowired
    private ResultMessage resultMessage;
    @Autowired
    private CarouselService carouselService;

    @GetMapping("/resources/carousel")
    public ResultMessage carousels() {
        List<Carousel> carousels = carouselService.getCarouselList();
        resultMessage.success("001", carousels);
        return resultMessage;
    }

}
