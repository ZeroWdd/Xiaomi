package com.mall.xiaomi.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResponseMessage {
    private String errorCode;
    private String errorMsg;
    private Map<String, Object> objectMap = new HashMap<>();

    public ResponseMessage addObject(String key, Object value) {
        this.objectMap.put(key, value);
        return this;
    }

    public static ResponseMessage success() {
        ResponseMessage rm = new ResponseMessage();
        rm.setErrorCode("100");
        rm.setErrorMsg("处理成功");
        return rm;
    }

    public static ResponseMessage error() {
        ResponseMessage rm = new ResponseMessage();
        rm.setErrorCode("200");
        rm.setErrorMsg("处理失败");
        return rm;
    }
}
