package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "carousel")
public class Carousel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "JDBC")
    @Column(name = "carousel_id")
    private Integer carouselId;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "describes")
    private String describes;

}