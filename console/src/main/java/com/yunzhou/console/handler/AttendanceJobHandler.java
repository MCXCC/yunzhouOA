package com.yunzhou.console.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AttendanceJobHandler {

    @Scheduled(cron = "0 0 23 * * ?") // 每天晚上11点执行
    public void dailyAttendanceHandler() {
        log.info("每日考勤统计任务开始执行");
        // TODO: 扫描当日打卡记录，计算迟到、早退、缺勤
        log.info("每日考勤统计任务执行完成");
    }

    @Scheduled(cron = "0 0 0 1 * ?") // 每月1号凌晨执行
    public void attendanceMonthlyReportHandler() {
        log.info("月度考勤报表生成任务开始执行");
        // TODO: 生成月度考勤报表
        log.info("月度考勤报表生成任务执行完成");
    }
}
