package com.yunzhou.console.service.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.model.system.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final DSLContext dsl;

    private static final String SYS_USER = "sys_user";

    public SysUser getUserById(Long id) {
        return dsl.selectFrom(table(SYS_USER))
                .where(field("id").eq(id))
                .and(field("deleted").eq("0"))
                .fetchOneInto(SysUser.class);
    }

    public SysUser getUserByUsername(String username) {
        return dsl.selectFrom(table(SYS_USER))
                .where(field("username").eq(username))
                .and(field("deleted").eq("0"))
                .fetchOneInto(SysUser.class);
    }

    public PageResult<SysUser> listUsers(String keyword, Integer status, Long deptId, Integer pageNum, Integer pageSize) {
        var condition = field("deleted").eq("0");

        if (keyword != null && !keyword.isEmpty()) {
            condition = condition.and(
                    field("username").like("%" + keyword + "%")
                            .or(field("nickname").like("%" + keyword + "%"))
                            .or(field("email").like("%" + keyword + "%"))
                            .or(field("phone").like("%" + keyword + "%"))
            );
        }
        if (status != null) {
            condition = condition.and(field("status").eq(status.toString()));
        }
        if (deptId != null) {
            condition = condition.and(field("dept_id").eq(deptId));
        }

        Long total = dsl.selectCount()
                .from(table(SYS_USER))
                .where(condition)
                .fetchOneInto(Long.class);

        List<SysUser> records = dsl.selectFrom(table(SYS_USER))
                .where(condition)
                .orderBy(field("create_time").desc())
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetchInto(SysUser.class);

        return PageResult.of(records, total, pageNum, pageSize);
    }

    @Transactional
    public void createUser(SysUser user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setDeleted("0");

        dsl.insertInto(table(SYS_USER))
                .set(field("dept_id"), user.getDeptId())
                .set(field("username"), user.getUsername())
                .set(field("nickname"), user.getNickname())
                .set(field("email"), user.getEmail())
                .set(field("phone"), user.getPhone())
                .set(field("gender"), user.getGender())
                .set(field("avatar"), user.getAvatar())
                .set(field("password"), user.getPassword())
                .set(field("status"), user.getStatus() != null ? user.getStatus() : "0")
                .set(field("create_time"), now)
                .set(field("update_time"), now)
                .set(field("deleted"), "0")
                .set(field("remark"), user.getRemark())
                .execute();
    }

    @Transactional
    public void updateUser(SysUser user) {
        user.setUpdateTime(LocalDateTime.now());

        dsl.update(table(SYS_USER))
                .set(field("dept_id"), user.getDeptId())
                .set(field("nickname"), user.getNickname())
                .set(field("email"), user.getEmail())
                .set(field("phone"), user.getPhone())
                .set(field("gender"), user.getGender())
                .set(field("avatar"), user.getAvatar())
                .set(field("status"), user.getStatus())
                .set(field("update_time"), user.getUpdateTime())
                .set(field("remark"), user.getRemark())
                .where(field("id").eq(user.getId()))
                .and(field("deleted").eq("0"))
                .execute();
    }

    @Transactional
    public void deleteUser(Long id) {
        dsl.update(table(SYS_USER))
                .set(field("deleted"), "1")
                .set(field("update_time"), LocalDateTime.now())
                .where(field("id").eq(id))
                .execute();
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        dsl.update(table(SYS_USER))
                .set(field("password"), newPassword)
                .set(field("update_time"), LocalDateTime.now())
                .where(field("id").eq(id))
                .and(field("deleted").eq("0"))
                .execute();
    }
}