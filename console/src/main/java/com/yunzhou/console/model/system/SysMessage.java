package com.yunzhou.console.model.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户消息")
public class SysMessage implements Serializable {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型（1系统 2待办 3提醒）")
    private String type;

    @Schema(description = "发送者ID")
    private Long senderId;

    @Schema(description = "接收者ID")
    private Long receiverId;

    @Schema(description = "阅读状态（0未读 1已读）")
    private String readStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "删除标志（0存在 1删除）")
    private String deleted;
}