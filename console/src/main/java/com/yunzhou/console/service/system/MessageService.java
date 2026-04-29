package com.yunzhou.console.service.system;

import com.yunzhou.console.common.PageResult;
import com.yunzhou.console.model.system.SysMessage;
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
public class MessageService {

    private final DSLContext dsl;

    private static final String SYS_MESSAGE = "sys_message";

    public PageResult<SysMessage> listMessages(Long receiverId, Integer readStatus, Integer pageNum, Integer pageSize) {
        var condition = field("deleted").eq("0");

        if (receiverId != null) {
            condition = condition.and(field("receiver_id").eq(receiverId));
        }
        if (readStatus != null) {
            condition = condition.and(field("read_status").eq(readStatus.toString()));
        }

        Long total = dsl.selectCount()
                .from(table(SYS_MESSAGE))
                .where(condition)
                .fetchOneInto(Long.class);

        List<SysMessage> records = dsl.selectFrom(table(SYS_MESSAGE))
                .where(condition)
                .orderBy(field("create_time").desc())
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetchInto(SysMessage.class);

        return PageResult.of(records, total, pageNum, pageSize);
    }

    public Long countUnread(Long receiverId) {
        return dsl.selectCount()
                .from(table(SYS_MESSAGE))
                .where(field("receiver_id").eq(receiverId))
                .and(field("read_status").eq("0"))
                .and(field("deleted").eq("0"))
                .fetchOneInto(Long.class);
    }

    public void markAsRead(Long id) {
        dsl.update(table(SYS_MESSAGE))
                .set(field("read_status"), "1")
                .where(field("id").eq(id))
                .and(field("deleted").eq("0"))
                .execute();
    }

    public void markAllAsRead(Long receiverId) {
        dsl.update(table(SYS_MESSAGE))
                .set(field("read_status"), "1")
                .where(field("receiver_id").eq(receiverId))
                .and(field("read_status").eq("0"))
                .and(field("deleted").eq("0"))
                .execute();
    }

    public void sendMessage(SysMessage message) {
        LocalDateTime now = LocalDateTime.now();
        message.setCreateTime(now);
        message.setDeleted("0");
        message.setReadStatus("0");

        dsl.insertInto(table(SYS_MESSAGE))
                .set(field("title"), message.getTitle())
                .set(field("content"), message.getContent())
                .set(field("type"), message.getType())
                .set(field("sender_id"), message.getSenderId())
                .set(field("receiver_id"), message.getReceiverId())
                .set(field("read_status"), "0")
                .set(field("create_time"), now)
                .set(field("deleted"), "0")
                .execute();
    }
}
