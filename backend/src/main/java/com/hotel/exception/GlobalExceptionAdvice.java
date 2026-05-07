package com.hotel.exception;

import com.hotel.common.BusinessException;
import com.hotel.common.RestResult;
import com.hotel.common.ResultCodeConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public RestResult<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return RestResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestResult<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("参数校验异常: {}", message);

        // 返回字段名和错误信息的映射
        Map<String, String> data = new HashMap<>();
        if (fieldError != null) {
            data.put("field", fieldError.getField());
            data.put("rejectedValue", fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "null");
            data.put("message", message);
        }

        return RestResult.error(ResultCodeConstant.BAD_REQUEST, message);
    }

    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public RestResult<Map<String, String>> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("参数绑定异常: {}", message);

        // 返回字段名和错误信息的映射
        Map<String, String> data = new HashMap<>();
        if (fieldError != null) {
            data.put("field", fieldError.getField());
            data.put("rejectedValue", fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "null");
            data.put("message", message);
        }

        return RestResult.error(ResultCodeConstant.BAD_REQUEST, message);
    }

    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public RestResult<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        // 开发环境下返回详细错误信息
        String errorMsg = e.getMessage();
        if (errorMsg != null && errorMsg.length() > 200) {
            errorMsg = errorMsg.substring(0, 200);
        }
        return RestResult.error("系统异常: " + errorMsg);
    }
}
