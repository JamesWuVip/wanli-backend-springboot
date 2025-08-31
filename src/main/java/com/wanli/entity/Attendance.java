package com.wanli.entity;

import com.wanli.entity.enums.AttendanceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤实体类
 * 用于管理学生考勤信息
 * 
 * @author JamesWu
 * @since 1.0.0
 */
@Entity
@Table(name = "attendances", indexes = {
    @Index(name = "idx_attendances_student_id", columnList = "student_id"),
    @Index(name = "idx_attendances_student_class_id", columnList = "student_class_id"),
    @Index(name = "idx_attendances_date", columnList = "date"),
    @Index(name = "idx_attendances_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_attendances_student_class_date", columnNames = {"student_class_id", "date"})
})
@SQLDelete(sql = "UPDATE attendances SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseEntity {

    /**
     * 学生
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendances_student_id"))
    @NotNull(message = "学生不能为空")
    private User student;

    /**
     * 学生班级关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendances_student_class_id"))
    @NotNull(message = "学生班级关联不能为空")
    private StudentClass studentClass;

    /**
     * 考勤日期
     */
    @Column(name = "date", nullable = false)
    @NotNull(message = "考勤日期不能为空")
    private LocalDate date;

    /**
     * 考勤状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "考勤状态不能为空")
    private AttendanceStatus status;

    /**
     * 签到时间
     */
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    /**
     * 签退时间
     */
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    /**
     * 迟到分钟数
     */
    @Column(name = "late_minutes")
    private Integer lateMinutes;

    /**
     * 早退分钟数
     */
    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes;

    /**
     * 请假原因
     */
    @Column(name = "excuse_reason", length = 500)
    private String excuseReason;

    /**
     * 备注
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * 签到IP地址
     */
    @Column(name = "check_in_ip", length = 45)
    private String checkInIp;

    /**
     * 签退IP地址
     */
    @Column(name = "check_out_ip", length = 45)
    private String checkOutIp;

    /**
     * 签到位置（JSON格式）
     */
    @Column(name = "check_in_location", columnDefinition = "TEXT")
    private String checkInLocation;

    /**
     * 签退位置（JSON格式）
     */
    @Column(name = "check_out_location", columnDefinition = "TEXT")
    private String checkOutLocation;

    /**
     * 是否手动标记
     */
    @Column(name = "is_manual", nullable = false)
    private Boolean isManual = false;

    /**
     * 手动标记者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by", foreignKey = @ForeignKey(name = "fk_attendances_marked_by"))
    private User markedBy;

    /**
     * 手动标记时间
     */
    @Column(name = "marked_at")
    private LocalDateTime markedAt;

    /**
     * 手动标记原因
     */
    @Column(name = "mark_reason", length = 500)
    private String markReason;

    /**
     * 检查是否出席
     * 
     * @return 是否出席
     */
    public boolean isPresent() {
        return AttendanceStatus.PRESENT.equals(status);
    }

    /**
     * 检查是否缺席
     * 
     * @return 是否缺席
     */
    public boolean isAbsent() {
        return AttendanceStatus.ABSENT.equals(status);
    }

    /**
     * 检查是否迟到
     * 
     * @return 是否迟到
     */
    public boolean isLate() {
        return AttendanceStatus.LATE.equals(status);
    }

    /**
     * 检查是否早退
     * 
     * @return 是否早退
     */
    public boolean isEarlyLeave() {
        return AttendanceStatus.EARLY_LEAVE.equals(status);
    }

    /**
     * 检查是否请假
     * 
     * @return 是否请假
     */
    public boolean isExcused() {
        return AttendanceStatus.EXCUSED.equals(status);
    }

    /**
     * 签到
     * 
     * @param checkInTime 签到时间
     * @param ip IP地址
     * @param location 位置信息
     */
    public void checkIn(LocalDateTime checkInTime, String ip, String location) {
        this.checkInTime = checkInTime;
        this.checkInIp = ip;
        this.checkInLocation = location;
        this.status = AttendanceStatus.PRESENT;
    }

    /**
     * 签退
     * 
     * @param checkOutTime 签退时间
     * @param ip IP地址
     * @param location 位置信息
     */
    public void checkOut(LocalDateTime checkOutTime, String ip, String location) {
        this.checkOutTime = checkOutTime;
        this.checkOutIp = ip;
        this.checkOutLocation = location;
    }

    /**
     * 标记为迟到
     * 
     * @param lateMinutes 迟到分钟数
     */
    public void markAsLate(Integer lateMinutes) {
        this.status = AttendanceStatus.LATE;
        this.lateMinutes = lateMinutes;
    }

    /**
     * 标记为早退
     * 
     * @param earlyLeaveMinutes 早退分钟数
     */
    public void markAsEarlyLeave(Integer earlyLeaveMinutes) {
        this.status = AttendanceStatus.EARLY_LEAVE;
        this.earlyLeaveMinutes = earlyLeaveMinutes;
    }

    /**
     * 标记为请假
     * 
     * @param reason 请假原因
     */
    public void markAsExcused(String reason) {
        this.status = AttendanceStatus.EXCUSED;
        this.excuseReason = reason;
    }

    /**
     * 标记为缺席
     */
    public void markAsAbsent() {
        this.status = AttendanceStatus.ABSENT;
    }

    /**
     * 手动标记考勤
     * 
     * @param status 考勤状态
     * @param markedBy 标记者
     * @param reason 标记原因
     */
    public void manualMark(AttendanceStatus status, User markedBy, String reason) {
        this.status = status;
        this.isManual = true;
        this.markedBy = markedBy;
        this.markedAt = LocalDateTime.now();
        this.markReason = reason;
    }

    /**
     * 计算实际出勤时长（分钟）
     * 
     * @return 出勤时长
     */
    public Integer getActualAttendanceMinutes() {
        if (checkInTime != null && checkOutTime != null) {
            return (int) java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
        }
        return null;
    }

    /**
     * 检查是否已签到
     * 
     * @return 是否已签到
     */
    public boolean hasCheckedIn() {
        return checkInTime != null;
    }

    /**
     * 检查是否已签退
     * 
     * @return 是否已签退
     */
    public boolean hasCheckedOut() {
        return checkOutTime != null;
    }
}