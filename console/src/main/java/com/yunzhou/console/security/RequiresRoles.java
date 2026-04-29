package com.yunzhou.console.security;

import java.lang.annotation.*;

/**
 * 角色注解 - 要求用户具有指定角色
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRoles {
    String[] value();
}
