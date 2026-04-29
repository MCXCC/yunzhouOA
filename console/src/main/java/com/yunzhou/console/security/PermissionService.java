package com.yunzhou.console.security;

import com.yunzhou.console.config.JwtTokenProvider;
import com.yunzhou.console.model.system.SysUser;
import com.yunzhou.console.service.system.UserService;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DSLContext dsl;

    /**
     * 获取当前用户ID
     */
    public Long getCurrentUserId(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        SysUser user = userService.getUserByUsername(username);
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前用户名
     */
    public String getCurrentUsername(String token) {
        return jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
    }

    /**
     * 检查用户是否有指定权限
     */
    public boolean hasPermission(Long userId, String permission) {
        // 超级管理员拥有所有权限
        if (isAdmin(userId)) {
            return true;
        }

        List<String> permissions = dsl.selectDistinct(field("m.perms"))
                .from(table("sys_user_role").as("ur"))
                .join(table("sys_role").as("r")).on(field("ur.role_id").eq(field("r.id")))
                .join(table("sys_role_menu").as("rm")).on(field("r.id").eq(field("rm.role_id")))
                .join(table("sys_menu").as("m")).on(field("rm.menu_id").eq(field("m.id")))
                .where(field("ur.user_id").eq(userId))
                .and(field("r.deleted").eq("0"))
                .and(field("r.status").eq("0"))
                .and(field("m.deleted").eq("0"))
                .and(field("m.status").eq("0"))
                .and(field("m.perms").isNotNull())
                .fetchInto(String.class);

        return permissions.contains(permission);
    }

    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(Long userId, String role) {
        // 超级管理员拥有所有角色
        if (isAdmin(userId)) {
            return true;
        }

        List<String> roles = dsl.selectDistinct(field("r.role_key"))
                .from(table("sys_user_role").as("ur"))
                .join(table("sys_role").as("r")).on(field("ur.role_id").eq(field("r.id")))
                .where(field("ur.user_id").eq(userId))
                .and(field("r.deleted").eq("0"))
                .and(field("r.status").eq("0"))
                .fetchInto(String.class);

        return roles.contains(role);
    }

    /**
     * 检查用户是否是超级管理员
     */
    public boolean isAdmin(Long userId) {
        return getUserRoles(userId).contains("admin");
    }

    /**
     * 获取用户的所有权限
     */
    public List<String> getUserPermissions(Long userId) {
        return dsl.selectDistinct(field("m.perms"))
                .from(table("sys_user_role").as("ur"))
                .join(table("sys_role").as("r")).on(field("ur.role_id").eq(field("r.id")))
                .join(table("sys_role_menu").as("rm")).on(field("r.id").eq(field("rm.role_id")))
                .join(table("sys_menu").as("m")).on(field("rm.menu_id").eq(field("m.id")))
                .where(field("ur.user_id").eq(userId))
                .and(field("r.deleted").eq("0"))
                .and(field("r.status").eq("0"))
                .and(field("m.deleted").eq("0"))
                .and(field("m.status").eq("0"))
                .and(field("m.perms").isNotNull())
                .fetchInto(String.class);
    }

    /**
     * 获取用户的所有角色
     */
    public List<String> getUserRoles(Long userId) {
        return dsl.selectDistinct(field("r.role_key"))
                .from(table("sys_user_role").as("ur"))
                .join(table("sys_role").as("r")).on(field("ur.role_id").eq(field("r.id")))
                .where(field("ur.user_id").eq(userId))
                .and(field("r.deleted").eq("0"))
                .and(field("r.status").eq("0"))
                .fetchInto(String.class);
    }
}
