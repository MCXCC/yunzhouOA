package com.yunzhou.console.service.system;

import com.yunzhou.console.model.system.SysMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final DSLContext dsl;

    private static final String SYS_MENU = "sys_menu";
    private static final String SYS_ROLE_MENU = "sys_role_menu";

    public SysMenu getMenuById(Long id) {
        return dsl.selectFrom(table(SYS_MENU))
                .where(field("id").eq(id))
                .and(field("deleted").eq("0"))
                .fetchOneInto(SysMenu.class);
    }

    public List<SysMenu> listMenus(String menuName, Integer status) {
        var condition = field("deleted").eq("0");

        if (menuName != null && !menuName.isEmpty()) {
            condition = condition.and(field("menu_name").like("%" + menuName + "%"));
        }
        if (status != null) {
            condition = condition.and(field("status").eq(status.toString()));
        }

        List<SysMenu> allMenus = dsl.selectFrom(table(SYS_MENU))
                .where(condition)
                .orderBy(field("order_num").asc())
                .fetchInto(SysMenu.class);

        return buildMenuTree(allMenus);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        Map<Long, List<SysMenu>> childrenMap = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                buildChildren(menu, childrenMap);
                tree.add(menu);
            }
        }
        return tree;
    }

    private void buildChildren(SysMenu parent, Map<Long, List<SysMenu>> childrenMap) {
        List<SysMenu> children = childrenMap.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            for (SysMenu child : children) {
                buildChildren(child, childrenMap);
            }
        }
    }

    @Transactional
    public void createMenu(SysMenu menu) {
        LocalDateTime now = LocalDateTime.now();
        menu.setCreateTime(now);
        menu.setUpdateTime(now);
        menu.setDeleted("0");

        dsl.insertInto(table(SYS_MENU))
                .set(field("menu_name"), menu.getMenuName())
                .set(field("parent_id"), menu.getParentId())
                .set(field("order_num"), menu.getOrderNum())
                .set(field("path"), menu.getPath())
                .set(field("component"), menu.getComponent())
                .set(field("query"), menu.getQuery())
                .set(field("route_name"), menu.getRouteName())
                .set(field("is_frame"), menu.getIsFrame())
                .set(field("is_cache"), menu.getIsCache())
                .set(field("menu_type"), menu.getMenuType())
                .set(field("visible"), menu.getVisible() != null ? menu.getVisible() : "0")
                .set(field("status"), menu.getStatus() != null ? menu.getStatus() : "0")
                .set(field("perms"), menu.getPerms())
                .set(field("icon"), menu.getIcon())
                .set(field("create_time"), now)
                .set(field("update_time"), now)
                .set(field("deleted"), "0")
                .set(field("remark"), menu.getRemark())
                .execute();
    }

    @Transactional
    public void updateMenu(SysMenu menu) {
        menu.setUpdateTime(LocalDateTime.now());

        dsl.update(table(SYS_MENU))
                .set(field("menu_name"), menu.getMenuName())
                .set(field("parent_id"), menu.getParentId())
                .set(field("order_num"), menu.getOrderNum())
                .set(field("path"), menu.getPath())
                .set(field("component"), menu.getComponent())
                .set(field("query"), menu.getQuery())
                .set(field("route_name"), menu.getRouteName())
                .set(field("is_frame"), menu.getIsFrame())
                .set(field("is_cache"), menu.getIsCache())
                .set(field("menu_type"), menu.getMenuType())
                .set(field("visible"), menu.getVisible())
                .set(field("status"), menu.getStatus())
                .set(field("perms"), menu.getPerms())
                .set(field("icon"), menu.getIcon())
                .set(field("update_time"), menu.getUpdateTime())
                .set(field("remark"), menu.getRemark())
                .where(field("id").eq(menu.getId()))
                .and(field("deleted").eq("0"))
                .execute();
    }

    @Transactional
    public void deleteMenu(Long id) {
        dsl.update(table(SYS_MENU))
                .set(field("deleted"), "1")
                .set(field("update_time"), LocalDateTime.now())
                .where(field("id").eq(id))
                .execute();
    }

    public List<SysMenu> listMenusByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> menuIds = dsl.selectDistinct(field("menu_id"))
                .from(table(SYS_ROLE_MENU))
                .where(field("role_id").in(roleIds))
                .fetchInto(Long.class);

        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<SysMenu> menus = dsl.selectFrom(table(SYS_MENU))
                .where(field("id").in(menuIds))
                .and(field("deleted").eq("0"))
                .and(field("status").eq("0"))
                .orderBy(field("order_num").asc())
                .fetchInto(SysMenu.class);

        return buildMenuTree(menus);
    }
}