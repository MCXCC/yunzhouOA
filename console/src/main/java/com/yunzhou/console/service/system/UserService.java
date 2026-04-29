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

    public PageResult<SysUser> listUsers(String username, String phone, String status, Long deptId, Integer pageNum, Integer pageSize) {
        var condition = field("u.deleted").eq("0");

        if (username != null && !username.isEmpty()) {
            condition = condition.and(field("u.username").like("%" + username + "%"));
        }
        if (phone != null && !phone.isEmpty()) {
            condition = condition.and(field("u.phone").like("%" + phone + "%"));
        }
        if (status != null && !status.isEmpty()) {
            condition = condition.and(field("u.status").eq(status));
        }
        if (deptId != null) {
            condition = condition.and(field("u.dept_id").eq(deptId));
        }

        Long total = dsl.selectCount()
                .from(table(SYS_USER).as("u"))
                .where(condition)
                .fetchOneInto(Long.class);

        var records = dsl.select(
                        field("u.id").as("id"),
                        field("u.dept_id").as("dept_id"),
                        field("d.dept_name").as("dept_name"),
                        field("u.username").as("username"),
                        field("u.nickname").as("nickname"),
                        field("u.email").as("email"),
                        field("u.phone").as("phone"),
                        field("u.gender").as("gender"),
                        field("u.avatar").as("avatar"),
                        field("u.password").as("password"),
                        field("u.status").as("status"),
                        field("u.login_ip").as("login_ip"),
                        field("u.login_date").as("login_date"),
                        field("u.create_time").as("create_time"),
                        field("u.update_time").as("update_time"),
                        field("u.deleted").as("deleted"),
                        field("u.remark").as("remark")
                )
                .from(table(SYS_USER).as("u"))
                .leftJoin(table("sys_dept").as("d")).on(field("u.dept_id").eq(field("d.id")))
                .where(condition)
                .orderBy(field("u.create_time").desc())
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetch();

        List<SysUser> userList = records.map(r -> {
            SysUser user = new SysUser();
            user.setId(r.get(field("id", Long.class)));
            user.setDeptId(r.get(field("dept_id", Long.class)));
            user.setDeptName(r.get(field("dept_name", String.class)));
            user.setUsername(r.get(field("username", String.class)));
            user.setNickname(r.get(field("nickname", String.class)));
            user.setEmail(r.get(field("email", String.class)));
            user.setPhone(r.get(field("phone", String.class)));
            user.setGender(r.get(field("gender", String.class)));
            user.setAvatar(r.get(field("avatar", String.class)));
            user.setPassword(r.get(field("password", String.class)));
            user.setStatus(r.get(field("status", String.class)));
            user.setLoginIp(r.get(field("login_ip", String.class)));
            user.setLoginDate(r.get(field("login_date", java.sql.Timestamp.class)) != null ? r.get(field("login_date", java.sql.Timestamp.class)).toLocalDateTime() : null);
            user.setCreateTime(r.get(field("create_time", java.sql.Timestamp.class)) != null ? r.get(field("create_time", java.sql.Timestamp.class)).toLocalDateTime() : null);
            user.setUpdateTime(r.get(field("update_time", java.sql.Timestamp.class)) != null ? r.get(field("update_time", java.sql.Timestamp.class)).toLocalDateTime() : null);
            user.setDeleted(r.get(field("deleted", String.class)));
            user.setRemark(r.get(field("remark", String.class)));
            return user;
        });

        return PageResult.of(userList, total, pageNum, pageSize);
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