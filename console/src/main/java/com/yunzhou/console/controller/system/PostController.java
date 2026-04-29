package com.yunzhou.console.controller.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.common.R;
import com.yunzhou.console.model.system.SysPost;
import com.yunzhou.console.service.system.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "岗位管理", description = "岗位管理接口")
@RestController
@RequestMapping("/api/system/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "分页查询岗位列表")
    @GetMapping("/list")
    public R<PageResult<SysPost>> list(
            @RequestParam(required = false) String postCode,
            @RequestParam(required = false) String postName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysPost> page = postService.listPosts(postCode, postName, status, pageNum, pageSize);
        return R.ok(page);
    }

    @Operation(summary = "获取所有岗位")
    @GetMapping("/all")
    public R<List<SysPost>> all() {
        List<SysPost> list = postService.listAllPosts();
        return R.ok(list);
    }

    @Operation(summary = "获取岗位详情")
    @GetMapping("/{id}")
    public R<SysPost> getById(@PathVariable Long id) {
        SysPost post = postService.getPostById(id);
        return R.ok(post);
    }

    @Operation(summary = "新增岗位")
    @PostMapping
    public R<Void> create(@RequestBody SysPost post) {
        postService.createPost(post);
        return R.ok("新增成功");
    }

    @Operation(summary = "修改岗位")
    @PutMapping
    public R<Void> update(@RequestBody SysPost post) {
        postService.updatePost(post);
        return R.ok("修改成功");
    }

    @Operation(summary = "删除岗位")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return R.ok("删除成功");
    }
}