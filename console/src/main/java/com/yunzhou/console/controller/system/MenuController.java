package com.yunzhou.console.controller.system;

import com.yunzhou.console.common.R;
import com.yunzhou.console.model.system.SysMenu;
import com.yunzhou.console.service.system.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "菜单管理", description = "菜单管理接口")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

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
    public R<List<SysMenu>> roleMenus() {
        // TODO: 从SecurityContext获取当前用户ID，查询用户角色ID数组
        Long[] roleIds = new Long[]{1L}; // 临时硬编码
        List<SysMenu> menus = menuService.listMenusByRoleIds(roleIds);
        return R.ok(menus);
    }
}