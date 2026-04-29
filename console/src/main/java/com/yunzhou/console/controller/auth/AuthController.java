package com.yunzhou.console.controller.auth;

import com.yunzhou.console.common.R;
import com.yunzhou.console.config.JwtTokenProvider;
import com.yunzhou.console.model.system.SysMenu;
import com.yunzhou.console.model.system.SysUser;
import com.yunzhou.console.service.system.MenuService;
import com.yunzhou.console.service.system.PostService;
import com.yunzhou.console.service.system.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

@Tag(name = "认证接口", description = "用户登录、注册")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PostService postService;
    private final MenuService menuService;
    private final DSLContext dsl;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody LoginRequest request) {
        SysUser user = userService.getUserByUsername(request.getUsername());
        if (user == null) {
            return R.fail("用户不存在");
        }
        if ("1".equals(user.getStatus())) {
            return R.fail("账号已被停用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return R.fail("密码错误");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return R.ok("登录成功", response);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/user-info")
    public R<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        SysUser user = userService.getUserByUsername(username);
        if (user == null) {
            return R.fail("用户不存在");
        }
        
        // 查询用户角色ID
        List<Long> roleIds = dsl.select(field("ur.role_id"))
                .from(table("sys_user_role").as("ur"))
                .join(table("sys_role").as("r")).on(field("ur.role_id").eq(field("r.id")))
                .where(field("ur.user_id").eq(user.getId()))
                .and(field("r.deleted").eq("0"))
                .and(field("r.status").eq("0"))
                .fetchInto(Long.class);
        
        // 查询用户角色Key
        List<String> roles = dsl.select(field("r.role_key"))
                .from(table("sys_user_role").as("ur"))
                .join(table("sys_role").as("r")).on(field("ur.role_id").eq(field("r.id")))
                .where(field("ur.user_id").eq(user.getId()))
                .and(field("r.deleted").eq("0"))
                .and(field("r.status").eq("0"))
                .fetchInto(String.class);
        
        // 查询用户岗位
        List<String> posts = postService.getUserPostNames(user.getId());
        
        // 查询用户菜单权限
        List<SysMenu> menus = menuService.listMenusByRoleIds(roleIds);
        List<Map<String, Object>> menuList = buildMenuTree(menus, 0L);
        
        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("deptId", user.getDeptId());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("roles", roles);
        result.put("posts", posts);
        result.put("menus", menuList);
        
        return R.ok(result);
    }
    
    private List<Map<String, Object>> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", menu.getId());
                node.put("name", menu.getMenuName());
                node.put("path", menu.getPath());
                node.put("component", menu.getComponent());
                node.put("perms", menu.getPerms());
                node.put("menuType", menu.getMenuType());
                node.put("icon", menu.getIcon());
                node.put("visible", menu.getVisible());
                
                // 递归构建子菜单
                List<Map<String, Object>> children = buildMenuTree(menus, menu.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }
                
                tree.add(node);
            }
        }
        
        return tree;
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout() {
        return R.ok();
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
    }
}