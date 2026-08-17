package com.ecommerce.studentscorebackend.dto;

import java.util.List;

/**
 * 分页响应DTO
 *
 * 封装分页查询的结果，包含数据列表和分页信息
 *
 * @param <T> 数据类型
 */
public class PageResponse<T> {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码（从1开始）
     */
    private int page;

    /**
     * 每页记录数
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 构造函数
     *
     * @param records 数据列表
     * @param total 总记录数
     * @param page 当前页码
     * @param pageSize 每页记录数
     */
    public PageResponse(List<T> records, long total, int page, int pageSize) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        // 计算总页数
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    // Getters and Setters

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
