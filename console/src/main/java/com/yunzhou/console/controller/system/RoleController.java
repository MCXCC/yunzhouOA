package com.yunzhou.console.controller.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.common.R;
import com.yunzhou.console.model.system.SysRole;
import com.yunzhou.console.service.system.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "角色管理接口")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/list")
    public R<PageResult<SysRole>> list(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysRole> page = roleService.listRoles(roleName, status, pageNum, pageSize);
        return R.ok(page);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public R<SysRole> getById(@PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        return R.ok(role);
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public R<Void> create(@RequestBody RoleRequest request) {
        roleService.createRole(request.getRole(), request.getMenuIds());
        return R.ok("新增成功");
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public R<Void> update(@RequestBody RoleRequest request) {
        roleService.updateRole(request.getRole(), request.getMenuIds());
        return R.ok("修改成功");
    }

    @Operation(summary = "删除角色（支持批量）")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        for (Long id : ids) {
            roleService.deleteRole(id);
        }
        return R.ok("删除成功");
    }

    @Operation(summary = "获取角色菜单ID列表")
    @GetMapping("/menuIds/{roleId}")
    public R<List<Long>> menuIds(@PathVariable Long roleId) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return R.ok(menuIds);
    }

    @Data
    public static class RoleRequest {
        private SysRole role;
        private Long[] menuIds;
    }
}