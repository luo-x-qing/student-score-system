-- Student table
CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(32) NOT NULL UNIQUE COMMENT '学号，唯一且非空',
    name VARCHAR(64) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    class_name VARCHAR(64) NOT NULL COMMENT '班级',
    remarks TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- Subject table
CREATE TABLE subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(64) NOT NULL UNIQUE COMMENT '学科名称，全局唯一',
    weight_rate DECIMAL(10, 4) NOT NULL COMMENT '学科权重，必须大于0，最多四位小数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT chk_weight_rate CHECK (weight_rate > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学科表';

-- Exam type table
CREATE TABLE exam_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(64) NOT NULL UNIQUE COMMENT '考试类型名称，全局唯一',
    rate DECIMAL(5, 2) NOT NULL COMMENT '考试类型权重，范围0-100，最多两位小数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT chk_rate CHECK (rate >= 0 AND rate <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试类型表';

-- Student score table
CREATE TABLE student_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    subject_id BIGINT NOT NULL COMMENT '学科ID',
    exam_type_id BIGINT NOT NULL COMMENT '考试类型ID',
    score DECIMAL(5, 2) COMMENT '分数，范围0-100，两位小数，NULL表示未录入',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT chk_score CHECK (score IS NULL OR (score >= 0 AND score <= 100)),
    CONSTRAINT uk_student_subject_exam UNIQUE (student_id, subject_id, exam_type_id),
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE RESTRICT,
    CONSTRAINT fk_subject FOREIGN KEY (subject_id) REFERENCES subject(id) ON DELETE RESTRICT,
    CONSTRAINT fk_exam_type FOREIGN KEY (exam_type_id) REFERENCES exam_type(id) ON DELETE RESTRICT,
    INDEX idx_student_id (student_id),
    INDEX idx_subject_id (subject_id),
    INDEX idx_exam_type_id (exam_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生成绩表';
