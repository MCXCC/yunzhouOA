package com.yunzhou.console.controller;

import com.yunzhou.console.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "系统接口", description = "系统基础接口")
@RestController
@RequestMapping("/api")
public class SystemController {

    @Operation(summary = "系统健康检查")
    @GetMapping("/health")
    public R<Map<String, String>> health() {
        return R.ok(Map.of("status", "UP", "service", "yunzhou-oa"));
    }
}
