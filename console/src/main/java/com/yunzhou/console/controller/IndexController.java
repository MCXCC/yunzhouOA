package com.yunzhou.console.controller;

import com.yunzhou.console.common.R;
import com.yunzhou.console.service.IndexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "首页统计", description = "首页统计接口")
@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;

    @Operation(summary = "获取首页统计数据")
    @GetMapping("/statistics")
    public R<Map<String, Object>> statistics() {
        Map<String, Object> data = indexService.getStatistics();
        return R.ok(data);
    }
}