package com.yunzhou.console.controller.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.common.R;
import com.yunzhou.console.config.JwtTokenProvider;
import com.yunzhou.console.model.system.SysUser;
import com.yunzhou.console.security.RequiresPermissions;
import com.yunzhou.console.service.system.PostService;
import com.yunzhou.console.service.system.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.*;

@Tag(name = "用户管理", description = "用户管理接口")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DSLContext dsl;

    @RequiresPermissions("system:user:list")
    @Operation(summary = "分页查询用户列表")
    @GetMapping("/list")
    public R<PageResult<SysUser>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long deptId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysUser> page = userService.listUsers(keyword, status, deptId, pageNum, pageSize);
        return R.ok(page);
    }

    @RequiresPermissions("system:user:query")
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public R<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getUserById(id);
        return R.ok(user);
    }

    @RequiresPermissions("system:user:add")
    @Operation(summary = "新增用户")
    @PostMapping
    public R<Void> create(@RequestBody SysUser user) {
        userService.createUser(user);
        return R.ok("新增成功");
    }

    @RequiresPermissions("system:user:edit")
    @Operation(summary = "修改用户")
    @PutMapping
    public R<Void> update(@RequestBody SysUser user) {
        userService.updateUser(user);
        return R.ok("修改成功");
    }

    @RequiresPermissions("system:user:remove")
    @Operation(summary = "删除用户（支持批量）")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        for (Long id : ids) {
            userService.deleteUser(id);
        }
        return R.ok("删除成功");
    }

    @RequiresPermissions("system:user:resetPwd")
    @Operation(summary = "重置密码")
    @PutMapping("/resetPwd")
    public R<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getId(), request.getNewPassword());
        return R.ok("重置成功");
    }

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public R<Map<String, Object>> profile(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        SysUser user = userService.getUserByUsername(username);
        if (user == null) {
            return R.fail("用户不存在");
        }
        
        // 查询用户角色
        List<String> roleNames = dsl.select(field("r.role_name"))
                .from(table("sys_user_role").as("ur"))
                .join(table("sys_role").as("r")).on(field("ur.role_id").eq(field("r.id")))
                .where(field("ur.user_id").eq(user.getId()))
                .and(field("r.deleted").eq("0"))
                .fetchInto(String.class);
        
        // 查询用户岗位
        List<String> postNames = postService.getUserPostNames(user.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("avatar", user.getAvatar());
        result.put("deptId", user.getDeptId());
        result.put("createTime", user.getCreateTime());
        result.put("roles", roleNames);
        result.put("posts", postNames);
        return R.ok(result);
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public R<Void> updateProfile(@RequestHeader("Authorization") String token, @RequestBody SysUser user) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        SysUser currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return R.fail("用户不存在");
        }
        user.setId(currentUser.getId());
        userService.updateUser(user);
        return R.ok("修改成功");
    }

    @Data
    public static class ResetPasswordRequest {
        private Long id;
        private String newPassword;
    }
}