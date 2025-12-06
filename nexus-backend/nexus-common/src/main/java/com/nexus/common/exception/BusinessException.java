package com.nexus.common.exception;

import com.nexus.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误信息
     */
    private final String message;
    
    /**
     * 构造方法
     * @param resultCode 结果码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    
    /**
     * 构造方法
     * @param resultCode 结果码枚举
     * @param message 自定义错误信息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }
    
    /**
     * 构造方法
     * @param resultCode 结果码枚举
     * @param cause 原因
     */
    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    
    /**
     * 构造方法
     * @param resultCode 结果码枚举
     * @param message 自定义错误信息
     * @param cause 原因
     */
    public BusinessException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.code = resultCode.getCode();
        this.message = message;
    }
}