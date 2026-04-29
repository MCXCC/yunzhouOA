package com.yunzhou.console.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexService {

    private final DSLContext dsl;

    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 用户总数
        Long userCount = dsl.selectCount()
                .from(table("sys_user"))
                .where(field("deleted").eq("0"))
                .fetchOneInto(Long.class);
        statistics.put("userCount", userCount);
        
        // 部门总数
        Long deptCount = dsl.selectCount()
                .from(table("sys_dept"))
                .where(field("deleted").eq("0"))
                .fetchOneInto(Long.class);
        statistics.put("deptCount", deptCount);
        
        // 角色总数
        Long roleCount = dsl.selectCount()
                .from(table("sys_role"))
                .where(field("deleted").eq("0"))
                .fetchOneInto(Long.class);
        statistics.put("roleCount", roleCount);
        
        // 菜单总数
        Long menuCount = dsl.selectCount()
                .from(table("sys_menu"))
                .where(field("deleted").eq("0"))
                .fetchOneInto(Long.class);
        statistics.put("menuCount", menuCount);
        
        // 未读消息数（示例，获取用户ID为1的未读消息数）
        Long unreadMessageCount = dsl.selectCount()
                .from(table("sys_message"))
                .where(field("receiver_id").eq(1L))
                .and(field("read_status").eq("0"))
                .and(field("deleted").eq("0"))
                .fetchOneInto(Long.class);
        statistics.put("unreadMessageCount", unreadMessageCount);
        
        return statistics;
    }
}