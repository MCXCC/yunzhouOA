package com.yunzhou.console.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private List<T> records;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Integer pages;

    public static <T> PageResult<T> of(List<T> records, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> page = new PageResult<>();
        page.setRecords(records);
        page.setTotal(total);
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setPages((int) Math.ceil((double) total / pageSize));
        return page;
    }
}
