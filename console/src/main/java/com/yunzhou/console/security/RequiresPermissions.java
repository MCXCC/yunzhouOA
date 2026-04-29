package com.yunzhou.console.security;

import java.lang.annotation.*;

/**
 * 权限注解 - 要求用户具有指定权限
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    String[] value();
}
