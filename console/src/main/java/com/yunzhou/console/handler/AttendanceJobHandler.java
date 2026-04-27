package com.yunzhou.console.handler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AttendanceJobHandler {

    @XxlJob("dailyAttendanceHandler")
    public void dailyAttendanceHandler() {
        log.info("每日考勤统计任务开始执行");
        // TODO: 扫描当日打卡记录，计算迟到、早退、缺勤
        log.info("每日考勤统计任务执行完成");
    }

    @XxlJob("attendanceMonthlyReportHandler")
    public void attendanceMonthlyReportHandler() {
        log.info("月度考勤报表生成任务开始执行");
        // TODO: 生成月度考勤报表
        log.info("月度考勤报表生成任务执行完成");
    }
}
