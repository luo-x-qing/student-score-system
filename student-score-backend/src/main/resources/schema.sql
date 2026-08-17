-- Student table (H2 compatible)
CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    gender VARCHAR(10),
    class_name VARCHAR(64) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Subject table
CREATE TABLE subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(64) NOT NULL UNIQUE,
    weight_rate DECIMAL(10, 4) NOT NULL CHECK (weight_rate > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Exam type table
CREATE TABLE exam_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(64) NOT NULL UNIQUE,
    rate DECIMAL(5, 2) NOT NULL CHECK (rate >= 0 AND rate <= 100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Student score table
CREATE TABLE student_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    exam_type_id BIGINT NOT NULL,
    score DECIMAL(5, 2) CHECK (score IS NULL OR (score >= 0 AND score <= 100)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_student_subject_exam UNIQUE (student_id, subject_id, exam_type_id),
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_exam_type FOREIGN KEY (exam_type_id) REFERENCES exam_type(id)
);

-- Indexes for student_score
CREATE INDEX idx_student_id ON student_score(student_id);
CREATE INDEX idx_subject_id ON student_score(subject_id);
CREATE INDEX idx_exam_type_id ON student_score(exam_type_id);
