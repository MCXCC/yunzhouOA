package com.yunzhou.console.controller;

import com.yunzhou.console.common.R;
import com.yunzhou.console.config.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口", description = "用户登录、注册")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody LoginRequest request) {
        // TODO: 从数据库查询用户并验证密码
        String token = jwtTokenProvider.generateToken(1L, request.getUsername());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return R.ok("登录成功", response);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout() {
        return R.ok();
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
    }
}
