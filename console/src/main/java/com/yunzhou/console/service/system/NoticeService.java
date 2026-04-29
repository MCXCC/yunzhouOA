package com.yunzhou.console.service.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.model.system.SysNotice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final DSLContext dsl;

    private static final String SYS_NOTICE = "sys_notice";

    public SysNotice getNoticeById(Long id) {
        return dsl.selectFrom(table(SYS_NOTICE))
                .where(field("id").eq(id))
                .and(field("deleted").eq("0"))
                .fetchOneInto(SysNotice.class);
    }

    public PageResult<SysNotice> listNotices(String title, Integer type, Integer pageNum, Integer pageSize) {
        var condition = field("deleted").eq("0");

        if (title != null && !title.isEmpty()) {
            condition = condition.and(field("title").like("%" + title + "%"));
        }
        if (type != null) {
            condition = condition.and(field("type").eq(type.toString()));
        }

        Long total = dsl.selectCount()
                .from(table(SYS_NOTICE))
                .where(condition)
                .fetchOneInto(Long.class);

        List<SysNotice> records = dsl.selectFrom(table(SYS_NOTICE))
                .where(condition)
                .orderBy(field("create_time").desc())
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetchInto(SysNotice.class);

        return PageResult.of(records, total, pageNum, pageSize);
    }

    public void createNotice(SysNotice notice) {
        LocalDateTime now = LocalDateTime.now();
        notice.setCreateTime(now);
        notice.setUpdateTime(now);
        notice.setDeleted("0");

        dsl.insertInto(table(SYS_NOTICE))
                .set(field("title"), notice.getTitle())
                .set(field("type"), notice.getType())
                .set(field("content"), notice.getContent())
                .set(field("status"), notice.getStatus() != null ? notice.getStatus() : "0")
                .set(field("create_by"), notice.getCreateBy())
                .set(field("create_time"), now)
                .set(field("update_time"), now)
                .set(field("deleted"), "0")
                .set(field("remark"), notice.getRemark())
                .execute();
    }

    public void updateNotice(SysNotice notice) {
        notice.setUpdateTime(LocalDateTime.now());

        dsl.update(table(SYS_NOTICE))
                .set(field("title"), notice.getTitle())
                .set(field("type"), notice.getType())
                .set(field("content"), notice.getContent())
                .set(field("status"), notice.getStatus())
                .set(field("update_time"), notice.getUpdateTime())
                .set(field("remark"), notice.getRemark())
                .where(field("id").eq(notice.getId()))
                .and(field("deleted").eq("0"))
                .execute();
    }

    public void deleteNotice(Long id) {
        dsl.update(table(SYS_NOTICE))
                .set(field("deleted"), "1")
                .set(field("update_time"), LocalDateTime.now())
                .where(field("id").eq(id))
                .execute();
    }
}
