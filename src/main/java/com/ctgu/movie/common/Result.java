package com.ctgu.movie.common;

/**
 * 统一返回结构：所有接口都返回 Result 对象。
 * 前端只需判断 code == 200 即可知道请求是否成功。
 *
 * @param <T> 业务数据的类型
 */
public class Result<T> {

    /** 业务状态码，200 表示成功，其它值表示失败 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 真正的业务数据 */
    private T data;
    /** 服务器时间戳，便于前端排查问题 */
    private long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
