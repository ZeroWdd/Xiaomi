package com.mall.xiaomi.pojo;

import lombok.Data;

@Data
public class Consumer {

    private Integer id;

    private String username;

    private String password;

    private String email;

    private String phone;

    public Consumer(String username, String password) {
        this.username = username;
        this.password = password;
    }
}