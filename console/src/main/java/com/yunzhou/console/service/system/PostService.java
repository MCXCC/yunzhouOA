package com.yunzhou.console.service.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.model.system.SysPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final DSLContext dsl;

    private static final String SYS_POST = "sys_post";
    private static final String SYS_USER_POST = "sys_user_post";

    public SysPost getPostById(Long id) {
        return dsl.selectFrom(table(SYS_POST))
                .where(field("id").eq(id))
                .and(field("deleted").eq("0"))
                .fetchOneInto(SysPost.class);
    }

    public PageResult<SysPost> listPosts(String postCode, String postName, Integer status, Integer pageNum, Integer pageSize) {
        var condition = field("deleted").eq("0");

        if (postCode != null && !postCode.isEmpty()) {
            condition = condition.and(field("post_code").like("%" + postCode + "%"));
        }
        if (postName != null && !postName.isEmpty()) {
            condition = condition.and(field("post_name").like("%" + postName + "%"));
        }
        if (status != null) {
            condition = condition.and(field("status").eq(status.toString()));
        }

        Long total = dsl.selectCount()
                .from(table(SYS_POST))
                .where(condition)
                .fetchOneInto(Long.class);

        List<SysPost> records = dsl.selectFrom(table(SYS_POST))
                .where(condition)
                .orderBy(field("order_num"))
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetchInto(SysPost.class);

        return PageResult.of(records, total, pageNum, pageSize);
    }

    public List<SysPost> listAllPosts() {
        return dsl.selectFrom(table(SYS_POST))
                .where(field("deleted").eq("0"))
                .and(field("status").eq("0"))
                .orderBy(field("order_num"))
                .fetchInto(SysPost.class);
    }

    @Transactional
    public void createPost(SysPost post) {
        LocalDateTime now = LocalDateTime.now();
        post.setCreateTime(now);
        post.setUpdateTime(now);
        post.setDeleted("0");

        dsl.insertInto(table(SYS_POST))
                .set(field("post_code"), post.getPostCode())
                .set(field("post_name"), post.getPostName())
                .set(field("order_num"), post.getOrderNum() != null ? post.getOrderNum() : 0)
                .set(field("status"), post.getStatus() != null ? post.getStatus() : "0")
                .set(field("create_time"), now)
                .set(field("update_time"), now)
                .set(field("deleted"), "0")
                .set(field("remark"), post.getRemark())
                .execute();
    }

    @Transactional
    public void updatePost(SysPost post) {
        post.setUpdateTime(LocalDateTime.now());

        dsl.update(table(SYS_POST))
                .set(field("post_code"), post.getPostCode())
                .set(field("post_name"), post.getPostName())
                .set(field("order_num"), post.getOrderNum())
                .set(field("status"), post.getStatus())
                .set(field("update_time"), post.getUpdateTime())
                .set(field("remark"), post.getRemark())
                .where(field("id").eq(post.getId()))
                .and(field("deleted").eq("0"))
                .execute();
    }

    @Transactional
    public void deletePost(Long id) {
        dsl.update(table(SYS_POST))
                .set(field("deleted"), "1")
                .set(field("update_time"), LocalDateTime.now())
                .where(field("id").eq(id))
                .execute();
    }

    public List<Long> getUserPostIds(Long userId) {
        return dsl.select(field("post_id"))
                .from(table(SYS_USER_POST))
                .where(field("user_id").eq(userId))
                .fetchInto(Long.class);
    }

    public List<String> getUserPostNames(Long userId) {
        return dsl.select(field("p.post_name"))
                .from(table(SYS_USER_POST).as("up"))
                .join(table(SYS_POST).as("p")).on(field("up.post_id").eq(field("p.id")))
                .where(field("up.user_id").eq(userId))
                .and(field("p.deleted").eq("0"))
                .fetchInto(String.class);
    }
}