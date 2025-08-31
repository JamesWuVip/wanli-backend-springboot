package com.wanli.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生班级关联实体类
 * 用于管理学生与班级的关联关系
 * 
 * @author JamesWu
 * @since 1.0.0
 */
@Entity
@Table(name = "student_classes", indexes = {
    @Index(name = "idx_student_classes_student_id", columnList = "student_id"),
    @Index(name = "idx_student_classes_course_id", columnList = "course_id"),
    @Index(name = "idx_student_classes_enrolled_at", columnList = "enrolled_at")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_classes_student_course", columnNames = {"student_id", "course_id"})
})
@SQLDelete(sql = "UPDATE student_classes SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentClass extends BaseEntity {

    /**
     * 学生
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_classes_student_id"))
    @NotNull(message = "学生不能为空")
    private User student;

    /**
     * 课程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_classes_course_id"))
    @NotNull(message = "课程不能为空")
    private Course course;

    /**
     * 报名时间
     */
    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    /**
     * 完成时间
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 退课时间
     */
    @Column(name = "dropped_at")
    private LocalDateTime droppedAt;

    /**
     * 学习进度（百分比）
     */
    @Column(name = "progress", nullable = false)
    private Integer progress = 0;

    /**
     * 最终成绩
     */
    @Column(name = "final_grade")
    private Integer finalGrade;

    /**
     * 是否通过
     */
    @Column(name = "passed")
    private Boolean passed;

    /**
     * 学习时长（分钟）
     */
    @Column(name = "study_time_minutes", nullable = false)
    private Integer studyTimeMinutes = 0;

    /**
     * 最后学习时间
     */
    @Column(name = "last_studied_at")
    private LocalDateTime lastStudiedAt;

    /**
     * 学生评价
     */
    @Column(name = "student_feedback", columnDefinition = "TEXT")
    private String studentFeedback;

    /**
     * 学生评分
     */
    @Column(name = "student_rating")
    private Integer studentRating;

    /**
     * 教师评价
     */
    @Column(name = "teacher_feedback", columnDefinition = "TEXT")
    private String teacherFeedback;

    /**
     * 备注
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * 是否活跃
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 学生的考勤记录
     */
    @OneToMany(mappedBy = "studentClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    /**
     * 检查是否已完成课程
     * 
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return completedAt != null;
    }

    /**
     * 检查是否已退课
     * 
     * @return 是否已退课
     */
    public boolean isDropped() {
        return droppedAt != null;
    }

    /**
     * 检查是否通过课程
     * 
     * @return 是否通过
     */
    public boolean isPassed() {
        return Boolean.TRUE.equals(passed);
    }

    /**
     * 完成课程
     * 
     * @param grade 最终成绩
     */
    public void completeCourse(Integer grade) {
        this.completedAt = LocalDateTime.now();
        this.finalGrade = grade;
        this.progress = 100;
        this.passed = grade != null && grade >= 60; // 假设60分及格
    }

    /**
     * 退课
     */
    public void dropCourse() {
        this.droppedAt = LocalDateTime.now();
        this.isActive = false;
    }

    /**
     * 更新学习进度
     * 
     * @param newProgress 新的进度
     */
    public void updateProgress(Integer newProgress) {
        if (newProgress != null && newProgress >= 0 && newProgress <= 100) {
            this.progress = newProgress;
            this.lastStudiedAt = LocalDateTime.now();
        }
    }

    /**
     * 增加学习时长
     * 
     * @param minutes 学习时长（分钟）
     */
    public void addStudyTime(Integer minutes) {
        if (minutes != null && minutes > 0) {
            this.studyTimeMinutes += minutes;
            this.lastStudiedAt = LocalDateTime.now();
        }
    }

    /**
     * 设置学生评价和评分
     * 
     * @param feedback 评价内容
     * @param rating 评分（1-5）
     */
    public void setStudentFeedback(String feedback, Integer rating) {
        this.studentFeedback = feedback;
        if (rating != null && rating >= 1 && rating <= 5) {
            this.studentRating = rating;
        }
    }

    /**
     * 设置教师评价
     * 
     * @param feedback 评价内容
     */
    public void setTeacherFeedback(String feedback) {
        this.teacherFeedback = feedback;
    }

    /**
     * 检查学生是否活跃（最近30天内有学习记录）
     * 
     * @return 是否活跃
     */
    public boolean isRecentlyActive() {
        return lastStudiedAt != null && 
               lastStudiedAt.isAfter(LocalDateTime.now().minusDays(30));
    }
}