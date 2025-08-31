package com.wanli.entity.enums;

/**
 * 考勤状态枚举
 * 
 * @author JamesWu
 * @since 1.0.0
 */
public enum AttendanceStatus {
    /**
     * 出席
     */
    PRESENT,
    
    /**
     * 缺席
     */
    ABSENT,
    
    /**
     * 迟到
     */
    LATE,
    
    /**
     * 早退
     */
    EARLY_LEAVE,
    
    /**
     * 请假
     */
    EXCUSED
}