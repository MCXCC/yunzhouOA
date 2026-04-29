package com.yunzhou.console.security;

import com.yunzhou.console.common.R;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;

    @Around("@annotation(requiresPermissions)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermissions requiresPermissions) throws Throwable {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.fail(401, "未登录");
        }

        String[] permissions = requiresPermissions.value();
        boolean hasPermission = false;
        for (String permission : permissions) {
            if (permissionService.hasPermission(userId, permission)) {
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            return R.fail(403, "没有权限访问");
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(requiresRoles)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRoles requiresRoles) throws Throwable {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.fail(401, "未登录");
        }

        String[] roles = requiresRoles.value();
        boolean hasRole = false;
        for (String role : roles) {
            if (permissionService.hasRole(userId, role)) {
                hasRole = true;
                break;
            }
        }

        if (!hasRole) {
            return R.fail(403, "没有权限访问");
        }

        return joinPoint.proceed();
    }

    private Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        return permissionService.getCurrentUserId(token);
    }
}
