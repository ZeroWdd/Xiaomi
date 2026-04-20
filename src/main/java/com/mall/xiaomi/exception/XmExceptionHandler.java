package com.mall.xiaomi.exception;

import com.mall.xiaomi.util.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
public class XmExceptionHandler {

    @Autowired
    private ResultMessage resultMessage;

    @ExceptionHandler(XmException.class)
    public ResultMessage handleException(XmException e){
        ExceptionEnum em = e.getExceptionEnum();
        resultMessage.fail(em.getCode() + "", em.getMsg());
        return resultMessage;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultMessage handleValidationException(Exception e) {
        String errorMsg;
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            errorMsg = ex.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            errorMsg = ex.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        } else {
            errorMsg = "参数校验失败";
        }
        resultMessage.fail("002", errorMsg);
        return resultMessage;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultMessage handleGenericException(Exception e) {
        e.printStackTrace();
        resultMessage.fail("002", "系统异常：" + e.getMessage());
        return resultMessage;
    }
}
