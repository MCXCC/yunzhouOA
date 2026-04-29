package com.yunzhou.console.model.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "菜单信息")
public class SysMenu implements Serializable {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "显示顺序")
    private Integer orderNum;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "路由参数")
    private String query;

    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "是否为外链（0是 1否）")
    private String isFrame;

    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private String isCache;

    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    @Schema(description = "菜单状态（0显示 1隐藏）")
    private String visible;

    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除标志（0存在 1删除）")
    private String deleted;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "子菜单列表")
    private List<SysMenu> children;
}