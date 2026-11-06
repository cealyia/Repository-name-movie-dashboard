package com.ctgu.movie.common;

import org.springframework.http.HttpStatus;

/**
 * 业务异常。Service 层发现参数不合法、数据不存在时抛出它，
 * 由全局异常处理器统一转成 HTTP 状态码 + Result 结构。
 */
public class BusinessException extends RuntimeException {

    private final int code;
    private final HttpStatus status;

    public BusinessException(String message) {
        this(400, message, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(int code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(404, message, HttpStatus.NOT_FOUND);
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message, HttpStatus.BAD_REQUEST);
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
