package com.yunzhou.console.service.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.model.system.SysRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final DSLContext dsl;

    private static final String SYS_ROLE = "sys_role";
    private static final String SYS_ROLE_MENU = "sys_role_menu";

    public SysRole getRoleById(Long id) {
        return dsl.selectFrom(table(SYS_ROLE))
                .where(field("id").eq(id))
                .and(field("deleted").eq("0"))
                .fetchOneInto(SysRole.class);
    }

    public PageResult<SysRole> listRoles(String roleName, Integer status, Integer pageNum, Integer pageSize) {
        var condition = field("deleted").eq("0");

        if (roleName != null && !roleName.isEmpty()) {
            condition = condition.and(field("role_name").like("%" + roleName + "%"));
        }
        if (status != null) {
            condition = condition.and(field("status").eq(status.toString()));
        }

        Long total = dsl.selectCount()
                .from(table(SYS_ROLE))
                .where(condition)
                .fetchOneInto(Long.class);

        List<SysRole> records = dsl.selectFrom(table(SYS_ROLE))
                .where(condition)
                .orderBy(field("order_num").asc())
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetchInto(SysRole.class);

        return PageResult.of(records, total, pageNum, pageSize);
    }

    @Transactional
    public void createRole(SysRole role, Long[] menuIds) {
        LocalDateTime now = LocalDateTime.now();
        role.setCreateTime(now);
        role.setUpdateTime(now);
        role.setDeleted("0");

        dsl.insertInto(table(SYS_ROLE))
                .set(field("role_name"), role.getRoleName())
                .set(field("role_key"), role.getRoleKey())
                .set(field("order_num"), role.getOrderNum())
                .set(field("data_scope"), role.getDataScope())
                .set(field("status"), role.getStatus() != null ? role.getStatus() : "0")
                .set(field("create_time"), now)
                .set(field("update_time"), now)
                .set(field("deleted"), "0")
                .set(field("remark"), role.getRemark())
                .execute();

        Long roleId = dsl.lastID().longValue();
        if (menuIds != null && menuIds.length > 0) {
            insertRoleMenus(roleId, menuIds);
        }
    }

    @Transactional
    public void updateRole(SysRole role, Long[] menuIds) {
        role.setUpdateTime(LocalDateTime.now());

        dsl.update(table(SYS_ROLE))
                .set(field("role_name"), role.getRoleName())
                .set(field("role_key"), role.getRoleKey())
                .set(field("order_num"), role.getOrderNum())
                .set(field("data_scope"), role.getDataScope())
                .set(field("status"), role.getStatus())
                .set(field("update_time"), role.getUpdateTime())
                .set(field("remark"), role.getRemark())
                .where(field("id").eq(role.getId()))
                .and(field("deleted").eq("0"))
                .execute();

        dsl.deleteFrom(table(SYS_ROLE_MENU))
                .where(field("role_id").eq(role.getId()))
                .execute();

        if (menuIds != null && menuIds.length > 0) {
            insertRoleMenus(role.getId(), menuIds);
        }
    }

    @Transactional
    public void deleteRole(Long id) {
        dsl.update(table(SYS_ROLE))
                .set(field("deleted"), "1")
                .set(field("update_time"), LocalDateTime.now())
                .where(field("id").eq(id))
                .execute();

        dsl.deleteFrom(table(SYS_ROLE_MENU))
                .where(field("role_id").eq(id))
                .execute();
    }

    public List<Long> getRoleMenuIds(Long roleId) {
        return dsl.select(field("menu_id"))
                .from(table(SYS_ROLE_MENU))
                .where(field("role_id").eq(roleId))
                .fetchInto(Long.class);
    }

    private void insertRoleMenus(Long roleId, Long[] menuIds) {
        for (Long menuId : menuIds) {
            dsl.insertInto(table(SYS_ROLE_MENU))
                    .set(field("role_id"), roleId)
                    .set(field("menu_id"), menuId)
                    .execute();
        }
    }
}