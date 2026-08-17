package com.ecommerce.studentscorebackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 批量成绩保存请求DTO
 *
 * 用于接收前端批量保存成绩的请求数据
 * 支持一次性保存多条成绩记录
 */
public class ScoreBatchSaveRequest {

    /**
     * 成绩列表，至少包含一条记录
     */
    @NotEmpty(message = "成绩列表不能为空")
    @Valid
    private List<ScoreSaveRequest> scores;

    // Getters and Setters

    public List<ScoreSaveRequest> getScores() {
        return scores;
    }

    public void setScores(List<ScoreSaveRequest> scores) {
        this.scores = scores;
    }
}
