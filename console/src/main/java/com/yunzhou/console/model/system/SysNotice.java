package com.yunzhou.console.model.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "通知公告")
public class SysNotice implements Serializable {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告类型（1通知 2公告）")
    private String type;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "状态（0正常 1关闭）")
    private String status;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除标志（0存在 1删除）")
    private String deleted;

    @Schema(description = "备注")
    private String remark;
}