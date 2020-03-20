package com.mall.xiaomi.util;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 16:16
 * @Description:
 */
@Component
@Data
public class ResultMessage {

    private String code;
    private String msg;
    private Object data;

    public void success(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public void success(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public void success(String code, Object data) {
        this.code = code;
        this.msg = null;
        this.data = data;
    }

    public void fail(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

}
