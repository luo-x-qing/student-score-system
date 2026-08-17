package com.ecommerce.studentscorebackend.common;

/**
 * 统一API响应结构
 *
 * 用于封装所有REST API的响应，提供统一的数据格式
 *
 * @param <T> 响应数据的类型
 */
public class ApiResponse<T> {

    /**
     * 响应状态码
     * 200: 成功
     * 400: 客户端错误（参数校验失败等）
     * 404: 资源不存在
     * 409: 冲突（如学号重复、删除冲突等）
     * 500: 服务器内部错误
     */
    private int code;

    /**
     * 响应消息
     * 成功时为 "success"
     * 失败时为具体错误信息
     */
    private String message;

    /**
     * 响应数据
     * 成功时包含实际业务数据
     * 失败时可能为 null
     */
    private T data;

    /**
     * 请求时间戳
     * 用于调试和日志追踪
     */
    private long timestamp;

    /**
     * 私有构造函数，通过静态工厂方法创建实例
     */
    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 创建成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 创建成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /**
     * 创建错误响应
     *
     * @param code HTTP状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 创建参数校验失败响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null);
    }

    /**
     * 创建资源不存在响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }

    /**
     * 创建冲突响应（如唯一性约束冲突）
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> conflict(String message) {
        return new ApiResponse<>(409, message, null);
    }

    // Getters and Setters

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
