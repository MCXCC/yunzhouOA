package com.yunzhou.console.controller.system;

import com.yunzhou.console.common.R;
import com.yunzhou.console.model.system.SysDept;
import com.yunzhou.console.service.system.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理", description = "部门管理接口")
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @Operation(summary = "查询部门列表（树形）")
    @GetMapping("/list")
    public R<List<SysDept>> list(
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer status) {
        List<SysDept> depts = deptService.listDepts(deptName, status);
        return R.ok(depts);
    }

    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public R<SysDept> getById(@PathVariable Long id) {
        SysDept dept = deptService.getDeptById(id);
        return R.ok(dept);
    }

    @Operation(summary = "新增部门")
    @PostMapping
    public R<Void> create(@RequestBody SysDept dept) {
        deptService.createDept(dept);
        return R.ok("新增成功");
    }

    @Operation(summary = "修改部门")
    @PutMapping
    public R<Void> update(@RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return R.ok("修改成功");
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        deptService.deleteDept(id);
        return R.ok("删除成功");
    }
}