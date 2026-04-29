package com.yunzhou.console.controller;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.common.R;
import com.yunzhou.console.config.JwtTokenProvider;
import com.yunzhou.console.model.system.SysMessage;
import com.yunzhou.console.service.system.MessageService;
import com.yunzhou.console.service.system.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息管理", description = "消息管理接口")
@RestController
@RequestMapping("/api/system/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "分页查询消息列表")
    @GetMapping("/list")
    public R<PageResult<SysMessage>> list(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer readStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        Long currentUserId = userService.getUserByUsername(username).getId();
        PageResult<SysMessage> page = messageService.listMessages(currentUserId, readStatus, pageNum, pageSize);
        return R.ok(page);
    }

    @Operation(summary = "获取未读消息数")
    @GetMapping("/unreadCount")
    public R<Long> unreadCount(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        Long currentUserId = userService.getUserByUsername(username).getId();
        Long count = messageService.countUnread(currentUserId);
        return R.ok(count);
    }

    @Operation(summary = "标记已读")
    @PutMapping("/read/{id}")
    public R<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return R.ok("标记成功");
    }

    @Operation(summary = "全部标记已读")
    @PutMapping("/readAll")
    public R<Void> markAllAsRead(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        Long currentUserId = userService.getUserByUsername(username).getId();
        messageService.markAllAsRead(currentUserId);
        return R.ok("标记成功");
    }
}