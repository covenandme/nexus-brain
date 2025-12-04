package com.nexus.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {
    /**成功 */
    SUCCESS(200, "操作成功"),

    /**客户端 */
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "暂无登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOTFOUND(404,"请求的资源不存在"),
    
    /**服务端 */
    FAILED(500, "操作失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}