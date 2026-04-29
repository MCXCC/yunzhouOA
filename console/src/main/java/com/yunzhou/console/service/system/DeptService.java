package com.yunzhou.console.service.system;

import com.yunzhou.console.model.system.SysDept;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeptService {

    private final DSLContext dsl;

    private static final String SYS_DEPT = "sys_dept";

    public SysDept getDeptById(Long id) {
        return dsl.selectFrom(table(SYS_DEPT))
                .where(field("id").eq(id))
                .and(field("deleted").eq("0"))
                .fetchOneInto(SysDept.class);
    }

    public List<SysDept> listDepts(String deptName, Integer status) {
        var condition = field("deleted").eq("0");

        if (deptName != null && !deptName.isEmpty()) {
            condition = condition.and(field("dept_name").like("%" + deptName + "%"));
        }
        if (status != null) {
            condition = condition.and(field("status").eq(status.toString()));
        }

        List<SysDept> allDepts = dsl.selectFrom(table(SYS_DEPT))
                .where(condition)
                .orderBy(field("order_num").asc())
                .fetchInto(SysDept.class);

        return buildDeptTree(allDepts);
    }

    private List<SysDept> buildDeptTree(List<SysDept> depts) {
        Map<Long, List<SysDept>> childrenMap = depts.stream()
                .collect(Collectors.groupingBy(SysDept::getParentId));

        List<SysDept> tree = new ArrayList<>();
        for (SysDept dept : depts) {
            if (dept.getParentId() == null || dept.getParentId() == 0L) {
                buildChildren(dept, childrenMap);
                tree.add(dept);
            }
        }
        return tree;
    }

    private void buildChildren(SysDept parent, Map<Long, List<SysDept>> childrenMap) {
        List<SysDept> children = childrenMap.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            for (SysDept child : children) {
                buildChildren(child, childrenMap);
            }
        }
    }

    @Transactional
    public void createDept(SysDept dept) {
        LocalDateTime now = LocalDateTime.now();
        dept.setCreateTime(now);
        dept.setUpdateTime(now);
        dept.setDeleted("0");

        dsl.insertInto(table(SYS_DEPT))
                .set(field("parent_id"), dept.getParentId())
                .set(field("ancestors"), dept.getAncestors())
                .set(field("dept_name"), dept.getDeptName())
                .set(field("order_num"), dept.getOrderNum())
                .set(field("leader"), dept.getLeader())
                .set(field("phone"), dept.getPhone())
                .set(field("email"), dept.getEmail())
                .set(field("status"), dept.getStatus() != null ? dept.getStatus() : "0")
                .set(field("create_time"), now)
                .set(field("update_time"), now)
                .set(field("deleted"), "0")
                .execute();
    }

    @Transactional
    public void updateDept(SysDept dept) {
        dept.setUpdateTime(LocalDateTime.now());

        dsl.update(table(SYS_DEPT))
                .set(field("parent_id"), dept.getParentId())
                .set(field("ancestors"), dept.getAncestors())
                .set(field("dept_name"), dept.getDeptName())
                .set(field("order_num"), dept.getOrderNum())
                .set(field("leader"), dept.getLeader())
                .set(field("phone"), dept.getPhone())
                .set(field("email"), dept.getEmail())
                .set(field("status"), dept.getStatus())
                .set(field("update_time"), dept.getUpdateTime())
                .where(field("id").eq(dept.getId()))
                .and(field("deleted").eq("0"))
                .execute();
    }

    @Transactional
    public void deleteDept(Long id) {
        dsl.update(table(SYS_DEPT))
                .set(field("deleted"), "1")
                .set(field("update_time"), LocalDateTime.now())
                .where(field("id").eq(id))
                .execute();
    }
}