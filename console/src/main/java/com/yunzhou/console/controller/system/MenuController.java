package com.yunzhou.console.controller.system;

import com.yunzhou.console.common.R;
import com.yunzhou.console.config.JwtTokenProvider;
import com.yunzhou.console.model.system.SysMenu;
import com.yunzhou.console.model.system.SysUser;
import com.yunzhou.console.service.system.MenuService;
import com.yunzhou.console.service.system.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Tag(name = "菜单管理", description = "菜单管理接口")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DSLContext dsl;

    @Operation(summary = "查询菜单列表（树形）")
    @GetMapping("/list")
    public R<List<SysMenu>> list(
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) Integer status) {
        List<SysMenu> menus = menuService.listMenus(menuName, status);
        return R.ok(menus);
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public R<SysMenu> getById(@PathVariable Long id) {
        SysMenu menu = menuService.getMenuById(id);
        return R.ok(menu);
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public R<Void> create(@RequestBody SysMenu menu) {
        menuService.createMenu(menu);
        return R.ok("新增成功");
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    public R<Void> update(@RequestBody SysMenu menu) {
        menuService.updateMenu(menu);
        return R.ok("修改成功");
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "获取当前用户菜单（用于前端动态路由）")
    @GetMapping("/roleMenus")
    public R<List<SysMenu>> roleMenus(@RequestHeader("Authorization") String token) {
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
        
        List<SysMenu> menus = menuService.listMenusByRoleIds(roleIds);
        return R.ok(menus);
    }
}