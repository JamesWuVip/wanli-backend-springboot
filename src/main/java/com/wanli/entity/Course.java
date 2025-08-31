package com.wanli.entity;

import com.wanli.entity.enums.CourseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程实体类
 * 用于管理课程信息
 * 
 * @author JamesWu
 * @since 1.0.0
 */
@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_courses_creator_id", columnList = "creator_id"),
    @Index(name = "idx_courses_status", columnList = "status"),
    @Index(name = "idx_courses_start_date", columnList = "start_date"),
    @Index(name = "idx_courses_end_date", columnList = "end_date")
})
@SQLDelete(sql = "UPDATE courses SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {

    /**
     * 课程名称
     */
    @Column(name = "name", nullable = false, length = 200)
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 200, message = "课程名称长度不能超过200个字符")
    private String name;

    /**
     * 课程描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 课程封面图片URL
     */
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    /**
     * 课程价格
     */
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 课程时长（分钟）
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /**
     * 最大学生数
     */
    @Column(name = "max_students")
    private Integer maxStudents;

    /**
     * 当前学生数
     */
    @Column(name = "current_students", nullable = false)
    private Integer currentStudents = 0;

    /**
     * 课程开始时间
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * 课程结束时间
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * 课程状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CourseStatus status = CourseStatus.DRAFT;

    /**
     * 课程标签（JSON格式）
     */
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    /**
     * 课程要求
     */
    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    /**
     * 课程目标
     */
    @Column(name = "objectives", columnDefinition = "TEXT")
    private String objectives;

    /**
     * 课程大纲（JSON格式）
     */
    @Column(name = "syllabus", columnDefinition = "TEXT")
    private String syllabus;

    /**
     * 课程资源（JSON格式）
     */
    @Column(name = "resources", columnDefinition = "TEXT")
    private String resources;

    /**
     * 课程设置（JSON格式）
     */
    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings;

    /**
     * 是否允许自主报名
     */
    @Column(name = "allow_self_enrollment", nullable = false)
    private Boolean allowSelfEnrollment = true;

    /**
     * 是否需要审核
     */
    @Column(name = "require_approval", nullable = false)
    private Boolean requireApproval = false;

    /**
     * 课程评分
     */
    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    /**
     * 评分人数
     */
    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    /**
     * 课程浏览次数
     */
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    /**
     * 课程创建者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false, foreignKey = @ForeignKey(name = "fk_courses_creator_id"))
    @NotNull(message = "课程创建者不能为空")
    private User creator;

    /**
     * 课程的班级
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentClass> classes;

    /**
     * 检查课程是否已发布
     * 
     * @return 是否已发布
     */
    public boolean isPublished() {
        return CourseStatus.PUBLISHED.equals(status) || 
               CourseStatus.IN_PROGRESS.equals(status) || 
               CourseStatus.COMPLETED.equals(status);
    }

    /**
     * 检查课程是否可以报名
     * 
     * @return 是否可以报名
     */
    public boolean canEnroll() {
        return isPublished() && 
               allowSelfEnrollment && 
               (maxStudents == null || currentStudents < maxStudents) &&
               (startDate == null || startDate.isAfter(LocalDateTime.now()));
    }

    /**
     * 检查课程是否已满员
     * 
     * @return 是否已满员
     */
    public boolean isFull() {
        return maxStudents != null && currentStudents >= maxStudents;
    }

    /**
     * 增加学生数量
     */
    public void incrementStudentCount() {
        this.currentStudents++;
    }

    /**
     * 减少学生数量
     */
    public void decrementStudentCount() {
        if (this.currentStudents > 0) {
            this.currentStudents--;
        }
    }

    /**
     * 增加浏览次数
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 更新评分
     * 
     * @param newRating 新评分
     */
    public void updateRating(BigDecimal newRating) {
        if (this.rating == null) {
            this.rating = newRating;
            this.ratingCount = 1;
        } else {
            BigDecimal totalRating = this.rating.multiply(BigDecimal.valueOf(this.ratingCount));
            totalRating = totalRating.add(newRating);
            this.ratingCount++;
            this.rating = totalRating.divide(BigDecimal.valueOf(this.ratingCount), 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 检查课程是否已开始
     * 
     * @return 是否已开始
     */
    public boolean hasStarted() {
        return startDate != null && startDate.isBefore(LocalDateTime.now());
    }

    /**
     * 检查课程是否已结束
     * 
     * @return 是否已结束
     */
    public boolean hasEnded() {
        return endDate != null && endDate.isBefore(LocalDateTime.now());
    }
}