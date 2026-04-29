package com.yunzhou.console.controller.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
