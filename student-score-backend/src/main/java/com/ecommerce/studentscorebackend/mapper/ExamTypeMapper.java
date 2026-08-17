package com.ecommerce.studentscorebackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.studentscorebackend.entity.ExamType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试类型数据访问层接口
 * 继承 MyBatis-Plus BaseMapper，提供基础 CRUD 操作
 *
 * MyBatis-Plus 自动提供以下方法：
 * - insert: 插入一条记录
 * - deleteById: 根据ID删除
 * - updateById: 根据ID更新
 * - selectById: 根据ID查询
 * - selectList: 条件查询列表
 * - selectCount: 条件统计
 */
@Mapper
public interface ExamTypeMapper extends BaseMapper<ExamType> {
    // MyBatis-Plus 已提供基础 CRUD，暂不需要自定义 SQL
}
