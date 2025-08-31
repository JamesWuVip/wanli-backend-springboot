package com.wanli.entity;

import com.wanli.entity.enums.Gender;
import com.wanli.entity.enums.Role;
import com.wanli.entity.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * 用于管理系统用户信息
 * 
 * @author JamesWu
 * @since 1.0.0
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_username", columnList = "username", unique = true),
    @Index(name = "idx_users_phone", columnList = "phone"),
    @Index(name = "idx_users_role", columnList = "role"),
    @Index(name = "idx_users_status", columnList = "status")
})
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    /**
     * 邮箱
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码哈希
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    @NotBlank(message = "密码不能为空")
    private String passwordHash;

    /**
     * 全名
     */
    @Column(name = "full_name", length = 100)
    @Size(max = 100, message = "全名长度不能超过100个字符")
    private String fullName;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * 手机号
     */
    @Column(name = "phone", length = 20)
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phone;

    /**
     * 出生日期
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * 性别
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    /**
     * 用户角色
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /**
     * 用户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 邮箱验证状态
     */
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    /**
     * 邮箱验证时间
     */
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    /**
     * 登录失败次数
     */
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    /**
     * 账户锁定时间
     */
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    /**
     * 密码重置令牌
     */
    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    /**
     * 密码重置令牌过期时间
     */
    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;

    /**
     * 邮箱验证令牌
     */
    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    /**
     * 邮箱验证令牌过期时间
     */
    @Column(name = "email_verification_expires")
    private LocalDateTime emailVerificationExpires;

    /**
     * 用户偏好设置（JSON格式）
     */
    @Column(name = "preferences", columnDefinition = "TEXT")
    private String preferences;

    /**
     * 用户创建的课程
     */
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> createdCourses;

    /**
     * 用户参与的班级
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentClass> studentClasses;

    /**
     * 用户的考勤记录
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    /**
     * 检查用户是否被锁定
     * 
     * @return 是否被锁定
     */
    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    /**
     * 检查用户是否激活
     * 
     * @return 是否激活
     */
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(status);
    }

    /**
     * 检查邮箱是否已验证
     * 
     * @return 邮箱是否已验证
     */
    public boolean isEmailVerified() {
        return Boolean.TRUE.equals(emailVerified);
    }

    /**
     * 重置登录失败次数
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    /**
     * 增加登录失败次数
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    /**
     * 锁定账户
     * 
     * @param lockDuration 锁定时长（分钟）
     */
    public void lockAccount(int lockDuration) {
        this.lockedUntil = LocalDateTime.now().plusMinutes(lockDuration);
        this.status = UserStatus.LOCKED;
    }

    /**
     * 验证邮箱
     */
    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerifiedAt = LocalDateTime.now();
        this.emailVerificationToken = null;
        this.emailVerificationExpires = null;
    }

    /**
     * 更新最后登录信息
     * 
     * @param loginIp 登录IP
     */
    public void updateLastLogin(String loginIp) {
        this.lastLoginAt = LocalDateTime.now();
        this.lastLoginIp = loginIp;
        resetFailedLoginAttempts();
    }
}