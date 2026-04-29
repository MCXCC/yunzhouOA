package com.yunzhou.console.model.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "角色信息")
public class SysRole implements Serializable {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符串")
    private String roleKey;

    @Schema(description = "显示顺序")
    private Integer orderNum;

    @Schema(description = "数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）")
    private String dataScope;

    @Schema(description = "状态（0正常 1停用）")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除标志（0存在 1删除）")
    private String deleted;

    @Schema(description = "备注")
    private String remark;
}