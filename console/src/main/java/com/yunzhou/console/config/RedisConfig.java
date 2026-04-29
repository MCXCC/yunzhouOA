package com.yunzhou.console.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    // 使用Spring Boot自动配置的RedisTemplate
    // Spring Boot 4会自动使用JacksonJsonRedisSerializer进行序列化
}