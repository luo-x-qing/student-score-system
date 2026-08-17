package com.ecommerce.studentscorebackend.dto;

/**
 * 学生查询请求DTO
 *
 * 支持多种查询方式：
 * 1. 学号精确匹配或前缀匹配
 * 2. 姓名包含匹配
 * 3. 班级精确筛选
 * 4. 分页查询
 */
public class StudentQueryRequest {

    /**
     * 学号查询条件
     * 支持精确匹配（完整学号）或前缀匹配（部分学号）
     */
    private String studentNo;

    /**
     * 姓名查询条件
     * 使用模糊匹配（LIKE %name%）
     */
    private String name;

    /**
     * 班级筛选条件
     * 精确匹配班级名称
     */
    private String className;

    /**
     * 页码，从1开始
     * 默认值：1
     */
    private Integer page = 1;

    /**
     * 每页记录数
     * 允许值：10、20、50、100
     * 默认值：20
     * 最大值：100
     */
    private Integer pageSize = 20;

    // Getters and Setters

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        // 限制最大每页记录数为100
        if (pageSize != null && pageSize > 100) {
            this.pageSize = 100;
        } else {
            this.pageSize = pageSize;
        }
    }
}
