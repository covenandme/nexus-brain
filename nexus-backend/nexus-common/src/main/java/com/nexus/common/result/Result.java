package com.nexus.common.result;

import lombok.Data;

/**
 * 统一响应结果封装类
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 私有构造方法
     */
    private Result() {
    }
    
    /**
     * 私有构造方法
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应
     * @param data 数据
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应 
     * @param resultCode 响应状态码枚举
     * @param data 数据
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> success(ResultCode resultCode,T data) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), data);
    }

    /**
     * 成功响应
     * @param message 消息
     * @param data 数据
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), null);
    }

    /**
     * 失败响应
     * @param message 消息
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败响应
     * @param resultCode 响应状态码枚举
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败响应 
     * @param resultCode 响应状态码枚举
     * @param message 消息
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(ResultCode resultCode,String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    /**
     * 失败响应
     * @param code 状态码
     * @param message 消息
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
 
}