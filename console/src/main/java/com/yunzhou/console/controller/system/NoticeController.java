package com.yunzhou.console.controller.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.common.R;
import com.yunzhou.console.model.system.SysNotice;
import com.yunzhou.console.service.system.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "公告管理", description = "公告管理接口")
@RestController
@RequestMapping("/api/system/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "分页查询公告列表")
    @GetMapping("/list")
    public R<PageResult<SysNotice>> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysNotice> page = noticeService.listNotices(title, type, pageNum, pageSize);
        return R.ok(page);
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public R<SysNotice> getById(@PathVariable Long id) {
        SysNotice notice = noticeService.getNoticeById(id);
        return R.ok(notice);
    }

    @Operation(summary = "新增公告")
    @PostMapping
    public R<Void> create(@RequestBody SysNotice notice) {
        noticeService.createNotice(notice);
        return R.ok("新增成功");
    }

    @Operation(summary = "修改公告")
    @PutMapping
    public R<Void> update(@RequestBody SysNotice notice) {
        noticeService.updateNotice(notice);
        return R.ok("修改成功");
    }

    @Operation(summary = "删除公告（支持批量）")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        for (Long id : ids) {
            noticeService.deleteNotice(id);
        }
        return R.ok("删除成功");
    }
}