package com.yunzhou.console.handler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkflowJobHandler {

    @XxlJob("workflowTimeoutHandler")
    public void workflowTimeoutHandler() {
        log.info("流程超时检测任务开始执行");
        // TODO: 扫描超时未处理的审批任务，发送提醒或自动转交
        log.info("流程超时检测任务执行完成");
    }

    @XxlJob("workflowReminderHandler")
    public void workflowReminderHandler() {
        log.info("流程催办提醒任务开始执行");
        // TODO: 发送待办事项提醒通知
        log.info("流程催办提醒任务执行完成");
    }
}
